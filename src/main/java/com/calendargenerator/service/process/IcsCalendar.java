package com.calendargenerator.service.process;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.component.VTimezone;
import biweekly.io.TimezoneAssignment;
import biweekly.io.TimezoneInfo;
import biweekly.property.Method;
import com.calendargenerator.model.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimeZone;

public class IcsCalendar {
    private Logger log;
    private ICalendar ical;
    private ClassesScraper scraper;
    private List<Classes> classes;

    private IcsCalendar() {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.ical = new ICalendar();
        this.ical.setMethod(Method.REQUEST);
        this.ical.setTimezoneInfo(getWarsawTimeZoneInfo());
        this.scraper = new ClassesScraper();
    }

    public IcsCalendar(String groupId) throws Exception {
        this();
        this.classes = scraper.getLectureList(groupId);
    }

    private TimezoneInfo getWarsawTimeZoneInfo() {
        TimeZone timezone = TimeZone.getTimeZone("Europe/Warsaw");
        VTimezone component = new VTimezone("Europe/Warsaw");
        TimezoneInfo timezoneInfo = new TimezoneInfo();
        timezoneInfo.setDefaultTimezone(new TimezoneAssignment(timezone, component));
        return timezoneInfo;
    }

    private void generateIcsCalendar() {
        this.classes.forEach(element -> ical.addEvent(classesToCalendarEvent(element)));
        log.info("iCalendar generated with " + classes.size() + " classes");
    }

    private VEvent classesToCalendarEvent(Classes classes) {
        biweekly.component.VEvent event = new biweekly.component.VEvent();
        event.setSummary(classes.getName());
        event.setLocation(classes.getLocation());
        event.setDateStart(classes.getStartDate().getTime());
        event.setDateEnd(classes.getEndDate().getTime());
        event.setDescription(classes.getInstructor());
        return event;
    }

    public void removeClasses(String classesName) {
        this.classes.removeIf(element -> element.getName().contains(classesName));
        log.info(classesName + " was succesfully deleted");
    }

    public void removeClasses(String classesName, Integer classesDayInWeek) {
        this.classes.removeIf(element -> element.getName().contains(classesName) &&
                element.getStartDate().getTime().getDay() == classesDayInWeek);
        log.info(classesName + " on " + classesDayInWeek + " day of the week was succesfully deleted");
    }

    @Override
    public String toString() {
        generateIcsCalendar();
        log.info("Proceed to generate output string");
        return Biweekly.write(ical).go();
    }
}
