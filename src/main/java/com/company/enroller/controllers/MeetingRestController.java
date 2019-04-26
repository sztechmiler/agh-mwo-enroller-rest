package com.company.enroller.controllers;

import java.io.Console;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

import io.jsonwebtoken.lang.Objects;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		meetingService.deleteMeeting(meeting);
		return new ResponseEntity(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
		Meeting createdMeeting = meetingService.addMeeting(meeting);

		return new ResponseEntity<>("I did created it. Id was assigned autmatically " + createdMeeting,
				HttpStatus.CREATED);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") Long id,
			@RequestBody Participant participant) {
		Meeting meeting = meetingService.findById(id);
//		ParticipantService participantService = new ParticipantService();
		if (meeting == null) {
			return new ResponseEntity("Meeting do not exists. ", HttpStatus.NOT_FOUND);

		}
		Participant checkedParticipant = participantService.findByLogin(participant.getLogin());
		if (checkedParticipant == null) {
			participantService.addParticipant(participant);
			meeting.addParticipant(participant);
			meetingService.updateMeeting(meeting);
			return new ResponseEntity<>("Participant added " + meeting + " and participant created: " + participant,
					HttpStatus.CREATED);
		}
		meeting.addParticipant(checkedParticipant);
		meetingService.updateMeeting(meeting);
		return new ResponseEntity<>("Participant " + checkedParticipant + " added to " + meeting, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meetingService.getParticipants(id);
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to update. A meeting with id" + id + " do not  exist.",
					HttpStatus.NOT_FOUND);
		}
		meeting.setDate(updatedMeeting.getDate());
		meeting.setTitle(updatedMeeting.getTitle());
		meeting.setDescription(updatedMeeting.getDescription());
		meetingService.updateMeeting(meeting);
		return new ResponseEntity("I did update it: " + meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> getsMeetingParticipants(@PathVariable("id") long id, @PathVariable("login") String login) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		String msg = meetingService.removeParticipant(meeting, login);
		return new ResponseEntity(msg, HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, params = "sort")
	public ResponseEntity<?> sortMeetingByField(@RequestParam("sort") String sort) {
		Collection<Meeting> meetings = meetingService.getAllSorted(sort);
		return new ResponseEntity(meetings, HttpStatus.OK);
	}
	@RequestMapping(value = "", method = RequestMethod.GET, params = {"title", "description"})
	public ResponseEntity<?> serachByTitileAndDescription(@RequestParam("title") String title, @RequestParam("description") String description) {
		Collection<Meeting> meetings = meetingService.getMeetingsByTitleAndDescription(title, description);
		if (meetings == null ){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(	meetings, HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, params = {"title"})
	public ResponseEntity<?> serachByTitile(@RequestParam("title") String title) {
		Collection<Meeting> meetings = meetingService.getMeetingsByTitle(title);
		if (meetings == null ){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(	meetings, HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, params = {"description"})
	public ResponseEntity<?> serachByDescription(@RequestParam("description") String description) {
		Collection<Meeting> meetings = meetingService.getMeetingsByDescription( description);
		if (meetings == null ){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(	meetings, HttpStatus.OK);
	}

}
