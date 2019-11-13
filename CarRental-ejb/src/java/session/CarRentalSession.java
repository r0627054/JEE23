package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    private EntityManager entityManager;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(
                entityManager.createNamedQuery("getAllCompanies").
                        getResultList());
    }

    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return entityManager.
                createNamedQuery("getAvailableCarTypes", CarType.class)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            CarRentalCompany crc = entityManager.find(CarRentalCompany.class, company);
            Quote q = crc.createQuote(constraints, getRenterName());
            getQuotes().add(q);
            return q;

        } catch (Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                //        done.add(RentalStore.getRental(quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (Exception e) {
            for (Reservation r : done) {
                //      RentalStore.getRental(r.getRentalCompany()).cancelReservation(r);
            }
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public String getCheapestCar(Date start, Date end, String region) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Getters & Setters
    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    public String getRenterName() {
        return renter;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

}
