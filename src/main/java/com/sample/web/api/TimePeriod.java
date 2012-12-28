package com.sample.web.api;

import com.sample.web.api.json.JsonDateSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author: Iain Porter
 */
@XmlRootElement
public class TimePeriod {

    private Date startDate;
    private Date endDate;

    public TimePeriod() {

    }


    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean validate() {
        try {
            if(startDate.after(endDate)) {
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
