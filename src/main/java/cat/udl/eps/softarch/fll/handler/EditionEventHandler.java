package cat.udl.eps.softarch.fll.handler;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import cat.udl.eps.softarch.fll.domain.edition.Edition;
import cat.udl.eps.softarch.fll.domain.edition.Venue;
import cat.udl.eps.softarch.fll.exception.DomainValidationException;
import cat.udl.eps.softarch.fll.repository.edition.VenueRepository;

@Component
@RepositoryEventHandler
public class EditionEventHandler {

	private final VenueRepository venueRepository;

	public EditionEventHandler(VenueRepository venueRepository) {
		this.venueRepository = venueRepository;
	}

	@HandleBeforeCreate
	public void handleEditionBeforeCreate(Edition edition) {
		if (edition.getVenue() != null) {
			return;
		}
		String venueName = edition.getInputVenueName();
		if (venueName == null || venueName.isBlank()) {
			return;
		}
		Venue venue = venueRepository.findByName(venueName)
			.orElseGet(() -> createNewVenue(venueName, edition.getInputVenueCity()));
		edition.setVenue(venue);
	}

	private Venue createNewVenue(String name, String city) {
		if (city == null || city.isBlank()) {
			throw new DomainValidationException("VENUE_CITY_REQUIRED",
				"Venue '" + name + "' does not exist. Provide 'venueCity' to create a new venue.");
		}
		return venueRepository.save(Venue.create(name, city));
	}
}
