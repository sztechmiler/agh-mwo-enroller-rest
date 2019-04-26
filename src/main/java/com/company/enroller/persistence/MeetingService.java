package com.company.enroller.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.websocket.Session;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
		// TODO Auto-generated method stub
	}

	public Meeting addMeeting(Meeting meeting) {
		// TODO Auto-generated method stub
		Transaction transcation = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transcation.commit();
		return meeting;

	}

	public Meeting updateMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();

		return meeting;
	}

	public Collection<Participant> getParticipants(long id) {

		String hql = "SELECT p FROM Meeting m join m.participants p WHERE m.id = " + id;
		Query query = connector.getSession().createQuery(hql);
		Collection<Participant> meetingParticipants = query.list();
		return meetingParticipants;

	}

	public Meeting deleteMeeting(Meeting meeting) {

		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
		return meeting;

	}

	public String removeParticipant(Meeting meeting, String login) {

		boolean wasFound = false;
		Collection<Participant> participants = getParticipants(meeting.getId());
		for (Participant p : participants) {
			if (p.getLogin().equals(login)) {
				meeting.removeParticipant(p);
				wasFound = true;
			}
		}
		updateMeeting(meeting);
		return ((wasFound) ? "Participant was removed from meeting"
				: "Participant was not enrolled and thus not deleted");
	}

	public Collection<Meeting> getAllSorted(String sort) {
		String hql = "FROM Meeting ORDER BY " + sort;
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Collection<Meeting> getMeetingsByTitleAndDescription(String title, String description) {
		List<Meeting> returnedMeetings = new ArrayList<>();
		Collection<Meeting> meetings = getAll();
		for (Meeting m : meetings) {
			if (m.getTitle().contains(title) || m.getDescription().contains(description)) {
				returnedMeetings.add(m);
			}
		}
		return returnedMeetings;
	}

	public Collection<Meeting> getMeetingsByTitle(String title) {
		List<Meeting> returnedMeetings = new ArrayList<>();
		Collection<Meeting> meetings = getAll();
		for (Meeting m : meetings) {
			if (m.getTitle().contains(title)) {
				returnedMeetings.add(m);
			}
		}
		return returnedMeetings;
	}
	public Collection<Meeting> getMeetingsByDescription(String description) {
		List<Meeting> returnedMeetings = new ArrayList<>();
		Collection<Meeting> meetings = getAll();
		for (Meeting m : meetings) {
			if (m.getTitle().contains(description)) {
				returnedMeetings.add(m);
			}
		}
		return returnedMeetings;
	}
}
