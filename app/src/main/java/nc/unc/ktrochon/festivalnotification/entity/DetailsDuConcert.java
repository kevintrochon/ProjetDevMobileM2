package nc.unc.ktrochon.festivalnotification.entity;

import java.io.Serializable;

/**
 * Classe qui represente le json recu par l'API. pour la serialisation.
 */
public class DetailsDuConcert implements Serializable {
    private String code;
    private String message;
    private Data data;

    public DetailsDuConcert() {
    }

    public DetailsDuConcert(String code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DetailsDuConcert{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
