package com.thoughtworks.sea.oidc.utility

import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.crypto.RSADecrypter
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.util.X509CertUtils
import com.nimbusds.jwt.SignedJWT
import com.sun.org.apache.xml.internal.security.utils.Base64
import com.thoughtworks.sea.oidc.exception.InvalidArgumentException
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class ParserUtils {
    companion object {
        private const val RSA_ALGORITHM = "RSA"
        private const val PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----"
        private const val PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----"
        private const val PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----"
        private const val PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----"
        private const val CERTIFICATE_HEADER = "-----BEGIN CERTIFICATE-----"
        private const val EMPTY_STRING = ""

        internal fun decryptJWE(idToken: String, privateKeyPem: String): SignedJWT {
            val jweObject = JWEObject.parse(idToken)
            val privateKey = parsePrivateKey(privateKeyPem)
            jweObject.decrypt(RSADecrypter(privateKey))

            return jweObject.payload.toSignedJWT()
        }

        internal fun verifyJWS(signedJWT: SignedJWT, key: String): Boolean {
            val publicKey = when {
                key.startsWith(PUBLIC_KEY_HEADER) -> parsePublicKey(key)
                key.startsWith(CERTIFICATE_HEADER) -> extractPublicKeyFromCertificate(key)
                else -> throw InvalidArgumentException("Invalid Public Key or Certificate")
            }

            return signedJWT.verify(RSASSAVerifier(publicKey))
        }

        private fun parsePrivateKey(privateKeyPem: String): PrivateKey {
            val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)

            val encodedPrivateKey = privateKeyPem
                .replace(PRIVATE_KEY_HEADER, EMPTY_STRING)
                .replace(PRIVATE_KEY_FOOTER, EMPTY_STRING)
            val keySpec = PKCS8EncodedKeySpec(Base64.decode(encodedPrivateKey))

            return keyFactory.generatePrivate(keySpec)
        }

        private fun extractPublicKeyFromCertificate(certificate: String): RSAPublicKey {
            val x509Certificate = X509CertUtils.parse(certificate)

            return x509Certificate.publicKey as RSAPublicKey
        }

        private fun parsePublicKey(publicKeyPem: String): RSAPublicKey {
            val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)

            val encodedPublicKeyPem = publicKeyPem
                .replace(PUBLIC_KEY_HEADER, EMPTY_STRING)
                .replace(PUBLIC_KEY_FOOTER, EMPTY_STRING)
            val keySpec = X509EncodedKeySpec(Base64.decode(encodedPublicKeyPem))

            return keyFactory.generatePublic(keySpec) as RSAPublicKey
        }
    }
}
