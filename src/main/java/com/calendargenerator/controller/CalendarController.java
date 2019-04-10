package com.calendargenerator.controller;

import com.calendargenerator.model.ComplexSchedule;
import com.calendargenerator.model.SimpleSchedule;
import com.calendargenerator.service.CalendarService;
import org.jsondoc.core.annotation.*;
import org.jsondoc.core.pojo.ApiStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/calendar")
@Api(name = "UEK calendar generator API",
        description = "Provides list of methods that can manipulate with UEK schedule and output google ical file/URL directly to the calendar",
        stage = ApiStage.ALPHA)
@ApiErrors(apierrors = {
        @ApiError(code = "404", description = "DataNotFoundException"),
        @ApiError(code = "500", description = "ParseException"),
        @ApiError(code = "500", description = "IOException")
})
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @ApiMethod(description = "Get ical file using provided group id")
    @RequestMapping(value = "/{groupId}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> schedule(@ApiPathParam(name = "groupId", description = "Group id, example: 140781")
                                           @PathVariable String groupId) {
        return calendarService.getSchedule(groupId);
    }

    @ApiMethod(description = "Get ical file with previously generated id")
    @RequestMapping(value = "/modified/{calendarGeneratedId}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modifiedSchedule(@ApiPathParam(name = "calendarGeneratedId", description = "Id generated by POST method")
                                                   @PathVariable String calendarGeneratedId) {
        return calendarService.getModifiedSchedule(calendarGeneratedId);
    }

    @ApiMethod(description = "Get distinct lecture list using provided group id")
    @RequestMapping(value = "/distinct/{groupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleSchedule> distinctLectures(@ApiPathParam(name = "groupId", description = "Group id, example: 140781")
                                                           @PathVariable String groupId) {
        return calendarService.getDistinctLectures(groupId);
    }

    @ApiMethod(description = "Method is extracting data about mandatory lectures from request body, then returns unique id that redirect to this record")
    @RequestMapping(value = "/simple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> generateUniqueLink(@RequestBody() SimpleSchedule simpleSchedule) {
        return calendarService.generateUniqueLink(simpleSchedule);
    }

    @ApiMethod(description = "Method is extracting data about mandatory lectures from request body, then returns unique id that redirect to this record")
    @RequestMapping(value = "/complex", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> generateUniqueLink(@RequestBody ComplexSchedule complexSchedule) {
        return calendarService.generateUniqueLink(complexSchedule);
    }

    @ApiMethod(description = "Method for test purposes. It returns all combined groups from database")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<ComplexSchedule>> getAllComplexSchedules() {
        return calendarService.getAllComplexSchedulesFromDatabase();
    }

    @ApiMethod(description = "Method for test purposes. It returns specific groups from database")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<Optional<ComplexSchedule>> getComplexSchedule(@PathVariable UUID id) {
        return calendarService.getComplexSchedulesFromDatabase(id);
    }
}

