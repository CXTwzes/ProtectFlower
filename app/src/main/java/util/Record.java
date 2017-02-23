package util;

import org.litepal.crud.DataSupport;

/**
 * Created by WZES on 2017/2/15.
 */

public class Record extends DataSupport{
    String temperature;
    String humidity;
    String date;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Record(String humidity, String temperature) {

        this.humidity = humidity;
        this.temperature = temperature;
    }
}
