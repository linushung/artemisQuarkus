package io.linus.artemis.authorisation

import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.Claims
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.enterprise.context.RequestScoped

/*
* Ref: https://quarkus.io/guides/security#standard-security-annotations
* Ref: https://quarkus.io/guides/security-jwt
* Ref: https://www.tomitribe.com/blog/microprofile-json-web-token-jwt/
*
* Convert OpenSSH format to PEM (but PKCS#1)format key
* 1. Create new public/private key pair: ssh-keygen -t rsa -b 4096 -f privateKey -C "linushung@gmail.com"
* 2. Convert to PEM key: ssh-keygen -p -m PEM -f privateKey
*/

const val PRIVATE_KEY = "/mock/privateKey.pem"
const val VALID_PERIOD_SEC = 3600

data class JWTClaims(val sub: String, val upn: Long, val jti: String)

interface JWTService {
    fun fetchJWT(claims: JWTClaims) : String
}

fun readPrivateKey(keyFile: String): String? {
    val contentByte = ByteArray(4096)
    return MockIdentityManager::class.java.getResourceAsStream(keyFile)?.let {
        String(contentByte, 0, it.read(contentByte), Charsets.UTF_8)
    }
}

fun trimKey(encodedKey: String) : String {
    return encodedKey.run {
        replace("-----BEGIN (.*)-----".toRegex(), "")
                .replace("-----END (.*)-----".toRegex(), "")
                .replace("\r\n", "")
                .replace("\n", "")
                .trim()
    }
}

fun decodePrivateKey(encodedKey: String): PrivateKey? {
    return Base64.getDecoder().decode(encodedKey)?.let {
        val keySpec = PKCS8EncodedKeySpec(it)
        KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }
}

@RequestScoped
open class MockIdentityManager : JWTService {
    override fun fetchJWT(claims: JWTClaims): String {
        return readPrivateKey(PRIVATE_KEY)
                ?.let { trimKey(it) }
                ?.let { decodePrivateKey(it) }
                ?.let {
                    val currentTimeSec= (System.currentTimeMillis() / 1000)
                    Jwt.claims()
                            .issuer("artemis-MockIdentityManager")
                            .subject(claims.sub)
                            .issuedAt(currentTimeSec)
                            .expiresAt(currentTimeSec + VALID_PERIOD_SEC)
                            .upn(claims.upn.toString())
                            .groups(mutableSetOf("Admin", "User"))
                            .audience("angular-realworld")
                            .claim("jti", claims.jti)
                            .claim(Claims.auth_time.name, currentTimeSec)
                            .jws().signatureKeyId(PRIVATE_KEY).sign(it)

//                    with(Jwt.claims()) {
//                        issuer("artemis-MockIdentityManager")
//                        subject(claims.sub)
//                        upn(claims.upn.toString())
//                        audience("angular-realworld")
//                        groups(mutableSetOf("Admin", "User"))
//                        claim("jti", claims.jti)
//                        issuedAt(currentTimeSec)
//                        expiresAt(currentTimeSec + VALID_PERIOD_SEC)
//                        claim(Claims.auth_time.name, currentTimeSec)
//                        jws().signatureKeyId(PRIVATE_KEY).sign(it)
//                    }
                } ?: ""
    }
}

/**
 * The required minimum set of MP-JWT claims
 * typ: This JOSE header parameter identifies the token as an RFC7519 and must be "JWT".
 * alg: This JOSE header parameter identifies the cryptographic algorithm used to secure the JWT. MP-JWT requires the
 *      use of the RSASSA-PKCS1-v1_5 SHA-256 algorithm and must be specified as "RS256".
 * kid: This JOSE header parameter is a hint indicating which key was used to secure the JWT. i.e. "privateKey.pem"
 * iss: The MP-JWT issuer.
 * sub: Identifies the principal that is the subject of the JWT. See the "upn" claim for how this relates to the
 *      container java.security.Principal.
 * exp: Identifies the expiration time on or after which the JWT MUST NOT be accepted for processing. The processing of
 *      the "exp" claim requires that the current date/time MUST be before the expiration date/time listed in the "exp"
 *      claim.
 * iat: Identifies the time at which the JWT was issued. This claim can be used to determine the age of the JWT. Its
 *      value MUST be a number containing a NumericDate value.
 * jti: Provides a unique identifier for the JWT. The identifier value MUST be assigned in a manner that ensures that
 *      there is a negligible probability that the same value will be accidentally assigned to a different data object;
 *      if the application uses multiple issuers, collisions MUST be prevented among values produced by different
 *      issuers as well. The "jti" claim can be used to prevent the JWT from being replayed. The "jti" value is a
 *      case-sensitive string.
 * upn: This MP-JWT custom claim is the user principal name in the java.security.Principal interface, and is the caller
 *      principal name in javax.security.enterprise.identitystore.IdentityStore. If this claim is missing, fallback to
 *      the "preferred_username", OIDC Section 5.1 should be attempted, and if that claim is missing, fallback to the
 *      "sub" claim should be used.
 * groups: This MP-JWT custom claim is the list of group names that have been assigned to the principal of the MP-JWT.
 *         This typically will required a mapping at the application container level to application deployment roles,
 *         but a one-to-one between group names and application role names is required to be performed in addition to
 *         any other mapping.
 * */
