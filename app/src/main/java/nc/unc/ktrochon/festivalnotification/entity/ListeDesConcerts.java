package nc.unc.ktrochon.festivalnotification.entity;

import java.io.Serializable;
import java.util.Arrays;

public class ListeDesConcerts implements Serializable {
    private String code;
    private String message;
    private String[] data;

    public ListeDesConcerts() {
    }

    public ListeDesConcerts(String code, String message, String[] data) {
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

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListeDesConcerts{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
