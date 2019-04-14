package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		return connector.getSession().createCriteria(Participant.class).list();
	}
	public Participant findByLogin(String login) {
		return (Participant)connector.getSession().get(Participant.class, login);
//		Criteria criteria = connector.getSession().createCriteria("login", login);
//		Participant part = (Participant) criteria.add(Restrictions.eq("login", login));
//		return part;
	}

	public Participant addParticipant(Participant participant) {
		Transaction transcation = connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transcation.commit();
		return participant;
	}

	public void deleteParticipant(String login) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(findByLogin(login));
		transaction.commit();
		
	}

	public Participant updateParticipant(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(participant);
		transaction.commit();
		
		return participant;
		
		
	}

}
