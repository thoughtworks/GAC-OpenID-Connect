package org.thoughtworks.sea.keycloak.provider.singpass;

import static org.keycloak.common.util.UriUtils.checkUrl;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.RealmModel;

public class SingpassOIDCIdentityProviderConfig extends OAuth2IdentityProviderConfig {
  public static final String JWKS_URL = "jwksUrl";

  public static final String USE_JWKS_URL = "useJwksUrl";
  public static final String VALIDATE_SIGNATURE = "validateSignature";

  public SingpassOIDCIdentityProviderConfig(IdentityProviderModel identityProviderModel) {
    super(identityProviderModel);
  }

  public SingpassOIDCIdentityProviderConfig() {
    super();
  }

  public String getPrompt() {
    return getConfig().get("prompt");
  }

  public void setPrompt(String prompt) {
    getConfig().put("prompt", prompt);
  }

  public String getIssuer() {
    return getConfig().get("issuer");
  }

  public void setIssuer(String issuer) {
    getConfig().put("issuer", issuer);
  }

  public String getLogoutUrl() {
    return getConfig().get("logoutUrl");
  }

  public void setLogoutUrl(String url) {
    getConfig().put("logoutUrl", url);
  }

  public String getPublicKeySignatureVerifier() {
    return getConfig().get("publicKeySignatureVerifier");
  }

  public void setPublicKeySignatureVerifier(String signingCertificate) {
    getConfig().put("publicKeySignatureVerifier", signingCertificate);
  }

  public String getPublicKeySignatureVerifierKeyId() {
    return getConfig().get("publicKeySignatureVerifierKeyId");
  }

  public void setPublicKeySignatureVerifierKeyId(String publicKeySignatureVerifierKeyId) {
    getConfig().put("publicKeySignatureVerifierKeyId", publicKeySignatureVerifierKeyId);
  }

  public boolean isValidateSignature() {
    return Boolean.valueOf(getConfig().get("validateSignature"));
  }

  public void setValidateSignature(boolean validateSignature) {
    getConfig().put(VALIDATE_SIGNATURE, String.valueOf(validateSignature));
  }

  public boolean isUseJwksUrl() {
    return Boolean.valueOf(getConfig().get(USE_JWKS_URL));
  }

  public void setUseJwksUrl(boolean useJwksUrl) {
    getConfig().put(USE_JWKS_URL, String.valueOf(useJwksUrl));
  }

  public String getJwksUrl() {
    return getConfig().get(JWKS_URL);
  }

  public void setJwksUrl(String jwksUrl) {
    getConfig().put(JWKS_URL, jwksUrl);
  }

  public boolean isBackchannelSupported() {
    return Boolean.valueOf(getConfig().get("backchannelSupported"));
  }

  public void setBackchannelSupported(boolean backchannel) {
    getConfig().put("backchannelSupported", String.valueOf(backchannel));
  }

  public boolean isDisableUserInfoService() {
    String disableUserInfo = getConfig().get("disableUserInfo");
    return Boolean.parseBoolean(disableUserInfo);
  }

  public void setDisableUserInfoService(boolean disable) {
    getConfig().put("disableUserInfo", String.valueOf(disable));
  }

  public int getAllowedClockSkew() {
    String allowedClockSkew = getConfig().get(ALLOWED_CLOCK_SKEW);
    if (allowedClockSkew == null || allowedClockSkew.isEmpty()) {
      return 0;
    }
    try {
      return Integer.parseInt(getConfig().get(ALLOWED_CLOCK_SKEW));
    } catch (NumberFormatException e) {
      // ignore it and use default
      return 0;
    }
  }

  @Override
  public void validate(RealmModel realm) {
    super.validate(realm);
    SslRequired sslRequired = realm.getSslRequired();
    checkUrl(sslRequired, getJwksUrl(), "jwks_url");
    checkUrl(sslRequired, getLogoutUrl(), "logout_url");
  }
}
