package cubox.aero.skapi.type;

public enum FaceApiType {

    DEL_FACE_URL("/v1/faces"),
    ADD_FACE_URL("/v1/faces/image-json"),
    IDENTIFICATION_URL("/v1/identification"),
    SCORE_URL("/v1/verification/image-json"),
    FEATURE_URL("/v1/feature"),
    FEATURE_SCORE_URL("/v1/feature-score"),
    FACE_API_URL("https://ovms-api.k-faceid.com"),
    X_API_KEY("SKJRJR1A-B23C-C36B-49EE-3D1C76B8225A");

    private final String value;

    FaceApiType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}