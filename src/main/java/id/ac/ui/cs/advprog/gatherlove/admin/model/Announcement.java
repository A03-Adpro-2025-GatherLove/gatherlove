package id.ac.ui.cs.advprog.gatherlove.admin.model;

import java.util.Date;
import java.util.UUID;
import java.util.TimeZone;
import java.util.Calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Announcement {
    
    @Id
    private UUID id = UUID.randomUUID();
    
    private String title;
    
    private String content;
    
    private Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC+7")).getTime();
}
