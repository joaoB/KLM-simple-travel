package com.afkl.exercises.spring.locations;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Locale.ENGLISH;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afkl.exercises.spring.fares.Currency;
import com.afkl.exercises.spring.fares.Fare;
import com.afkl.exercises.spring.fares.FareDescriptor;

@RestController
public class TicketController {

	private final AirportRepository repository;

	@Autowired
	public TicketController(AirportRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(value = "airports/{key}", method = GET)
	public Callable<HttpEntity<Location>> show(
			@RequestParam(value = "lang", defaultValue = "en") String lang,
			@PathVariable("key") String key) {
		return () -> {
			return this.showAux(lang, key)
					.map(l -> new ResponseEntity<>(l, OK))
					.orElse(new ResponseEntity<>(NOT_FOUND));
		};
	}

	private Optional<Location> showAux(String lang, String key) {
		try {
			Thread.sleep(ThreadLocalRandom.current().nextLong(200, 800));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return repository.get(Locale.forLanguageTag(lang), key);
	}

	@RequestMapping(value = "/fares/{origin}/{destination}", method = GET)
	public Callable<FareDescriptor> calculateFare(
			@PathVariable("origin") String origin,
			@PathVariable("destination") String destination,
			@RequestParam(value = "currency", defaultValue = "EUR") String currency,
			@RequestParam(value = "lang", defaultValue = "en") String lang)
			throws Exception {

		Callable<Stream<Future<Optional<Location>>>> task = () -> {
			return this.getDetails(lang, origin, destination);
		};

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Stream<Future<Optional<Location>>>> future = executor
				.submit(task);

		return () -> {
			Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 6000));
			final Location o = repository.get(ENGLISH, origin).orElseThrow(
					IllegalArgumentException::new);
			final Location d = repository.get(ENGLISH, destination)
					.orElseThrow(IllegalArgumentException::new);
			final BigDecimal fare = new BigDecimal(ThreadLocalRandom.current()
					.nextDouble(100, 3500)).setScale(2, HALF_UP);

			
			String[] descriptions = this.getDescriptions(future);
			return new FareDescriptor(new Fare(fare.doubleValue(),
					Currency.valueOf(currency.toUpperCase()), o.getCode(),
					d.getCode()), descriptions[0], descriptions[1]);
		};
	}

	private String[] getDescriptions(
			Future<Stream<Future<Optional<Location>>>> loc) {
		String[] descriptions = new String[2];
		String err = "Could not find the description for the location";
		try {
			descriptions = loc.get().map(future -> {
				try {
					return future.get().get().getDescription();
				} catch (Exception e) {
					return err;
				}
			}).toArray(size -> new String[size]);
		} catch (InterruptedException | ExecutionException e) {
			descriptions[0] = err;
			descriptions[1] = err;
		}
		return descriptions;

	}

	private Stream<Future<Optional<Location>>> getDetails(String lang,
			String origin, String destination) {
		ExecutorService executor = Executors.newWorkStealingPool();

		List<Callable<Optional<Location>>> callables = Arrays.asList(
				() -> this.showAux(lang, origin),
				() -> this.showAux(lang, destination));

		Stream<Future<Optional<Location>>> location = null;

		try {
			location = executor.invokeAll(callables).stream();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return location;

	}

}
