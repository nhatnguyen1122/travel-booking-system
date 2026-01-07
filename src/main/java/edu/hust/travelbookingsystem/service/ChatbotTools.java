package edu.hust.travelbookingsystem.service;

import dev.langchain4j.agent.tool.Tool;
import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.entity.FlightSeat;
import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.entity.HotelBedroom;
import edu.hust.travelbookingsystem.repository.FlightRepository;
import edu.hust.travelbookingsystem.repository.FlightSeatRepository;
import edu.hust.travelbookingsystem.repository.HotelBedroomRepository;
import edu.hust.travelbookingsystem.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tools available to the AI chatbot for searching real data and web content.
 * These methods are annotated with @Tool so LangChain4j can invoke them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatbotTools {

    private final HotelRepository hotelRepository;
    private final HotelBedroomRepository hotelBedroomRepository;
    private final FlightRepository flightRepository;
    private final FlightSeatRepository flightSeatRepository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Tool("Search for hotels by destination/location. Returns hotel name, price, address, and number of floors. Use this when user asks about hotels in a specific location like 'Da Nang', 'Phu Quoc', 'Nha Trang', etc.")
    public String searchHotels(String destination) {
        log.info("Tool: Searching hotels for destination: {}", destination);
        try {
            List<Hotel> hotels = hotelRepository.findByDestination(destination);
            
            if (hotels.isEmpty()) {
                // Try to get all hotels if destination search returns empty
                hotels = hotelRepository.findAll();
                if (hotels.isEmpty()) {
                    return "No hotels found in the system.";
                }
                // Filter manually by address containing destination
                String destLower = destination.toLowerCase();
                hotels = hotels.stream()
                        .filter(h -> h.getAddress().toLowerCase().contains(destLower))
                        .collect(Collectors.toList());
                
                if (hotels.isEmpty()) {
                    return "No hotels found in " + destination + ". Available destinations include: Da Nang, Nha Trang, Phu Quoc, Ha Long, Hoi An, Sapa.";
                }
            }

            StringBuilder result = new StringBuilder();
            result.append("Found ").append(hotels.size()).append(" hotel(s) in ").append(destination).append(":\n\n");
            
            for (Hotel hotel : hotels) {
                result.append("🏨 **").append(hotel.getHotelName()).append("**\n");
                result.append("   📍 Address: ").append(hotel.getAddress()).append("\n");
                result.append("   💰 Price from: ").append(String.format("%,.0f", hotel.getHotelPriceFrom())).append(" VND/night\n");
                result.append("   🏢 Floors: ").append(hotel.getNumberFloor()).append("\n");
                result.append("   🔗 Hotel ID: ").append(hotel.getId()).append("\n\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error searching hotels: {}", e.getMessage(), e);
            return "Error searching hotels: " + e.getMessage();
        }
    }

    @Tool("Get all available hotels in the system. Use this when user asks about available hotels without specifying a destination.")
    public String getAllHotels() {
        log.info("Tool: Getting all hotels");
        try {
            List<Hotel> hotels = hotelRepository.findAll();
            
            if (hotels.isEmpty()) {
                return "No hotels found in the system.";
            }

            StringBuilder result = new StringBuilder();
            result.append("Found ").append(hotels.size()).append(" hotel(s) in our system:\n\n");
            
            for (Hotel hotel : hotels) {
                result.append("🏨 **").append(hotel.getHotelName()).append("**\n");
                result.append("   📍 ").append(hotel.getAddress()).append("\n");
                result.append("   💰 From ").append(String.format("%,.0f", hotel.getHotelPriceFrom())).append(" VND/night\n\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting all hotels: {}", e.getMessage(), e);
            return "Error retrieving hotels: " + e.getMessage();
        }
    }

    @Tool("Get rooms available in a specific hotel by hotel ID. Returns room number, type, and price. Use this when user wants to see rooms in a specific hotel.")
    public String getHotelRooms(Long hotelId) {
        log.info("Tool: Getting rooms for hotel ID: {}", hotelId);
        try {
            Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
            if (hotel == null) {
                return "Hotel with ID " + hotelId + " not found.";
            }

            List<HotelBedroom> rooms = hotelBedroomRepository.findByHotelId(hotelId);
            
            if (rooms.isEmpty()) {
                return "No rooms found for hotel: " + hotel.getHotelName();
            }

            StringBuilder result = new StringBuilder();
            result.append("Rooms at **").append(hotel.getHotelName()).append("**:\n\n");
            
            for (HotelBedroom room : rooms) {
                result.append("🛏️ Room ").append(room.getRoomNumber());
                result.append(" - ").append(room.getRoomType());
                result.append(" - ").append(String.format("%,.0f", room.getPrice())).append(" VND/night\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting hotel rooms: {}", e.getMessage(), e);
            return "Error retrieving rooms: " + e.getMessage();
        }
    }

    @Tool("Search for available flights. Returns airline, ticket class, price, dates, and available seats. Use this when user asks about flights or wants to book a flight.")
    public String searchFlights() {
        log.info("Tool: Searching all flights");
        try {
            List<Flight> flights = flightRepository.findAll();
            
            if (flights.isEmpty()) {
                return "No flights found in the system.";
            }

            StringBuilder result = new StringBuilder();
            result.append("Found ").append(flights.size()).append(" flight(s):\n\n");
            
            for (Flight flight : flights) {
                result.append("✈️ **").append(flight.getAirlineName()).append("** (").append(flight.getTicketClass()).append(")\n");
                result.append("   📅 ").append(DATE_FORMAT.format(flight.getCheckInDate()));
                result.append(" → ").append(DATE_FORMAT.format(flight.getCheckOutDate())).append("\n");
                result.append("   💰 Price: ").append(String.format("%,.0f", flight.getPrice())).append(" VND\n");
                result.append("   💺 Available seats: ").append(flight.getSeatAvailable()).append("/").append(flight.getNumberOfChairs()).append("\n");
                result.append("   🔗 Flight ID: ").append(flight.getId()).append("\n\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error searching flights: {}", e.getMessage(), e);
            return "Error searching flights: " + e.getMessage();
        }
    }

    @Tool("Get available seats for a specific flight by flight ID. Returns seat numbers that can be booked. Use this when user wants to select specific seats on a flight.")
    public String getAvailableSeats(Long flightId) {
        log.info("Tool: Getting available seats for flight ID: {}", flightId);
        try {
            Flight flight = flightRepository.findById(flightId).orElse(null);
            if (flight == null) {
                return "Flight with ID " + flightId + " not found.";
            }

            List<FlightSeat> availableSeats = flightSeatRepository.findAvailableSeatsByFlightId(flightId);
            
            if (availableSeats.isEmpty()) {
                return "No available seats for flight " + flight.getAirlineName() + ". The flight may be fully booked.";
            }

            StringBuilder result = new StringBuilder();
            result.append("Available seats on **").append(flight.getAirlineName()).append("**:\n\n");
            result.append("💺 ");
            
            List<String> seatNumbers = availableSeats.stream()
                    .map(FlightSeat::getSeatNumber)
                    .collect(Collectors.toList());
            
            result.append(String.join(", ", seatNumbers));
            result.append("\n\n");
            result.append("Total available: ").append(availableSeats.size()).append(" seat(s)");
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error getting available seats: {}", e.getMessage(), e);
            return "Error retrieving seats: " + e.getMessage();
        }
    }

    @Tool("Search the internet for travel information using DuckDuckGo. Use this when user asks about travel tips, weather, attractions, or information not in our database. Query should be in English.")
    public String searchWeb(String query) {
        log.info("Tool: Web search for: {}", query);
        try {
            String encodedQuery = URLEncoder.encode(query + " Vietnam travel", StandardCharsets.UTF_8);
            String searchUrl = "https://html.duckduckgo.com/html/?q=" + encodedQuery;
            
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements results = doc.select(".result");
            
            if (results.isEmpty()) {
                return "No web search results found for: " + query;
            }

            StringBuilder result = new StringBuilder();
            result.append("🌐 **Web Search Results for \"").append(query).append("\":**\n\n");
            
            int count = 0;
            for (Element searchResult : results) {
                if (count >= 3) break; // Limit to 3 results
                
                Element titleElement = searchResult.selectFirst(".result__title");
                Element snippetElement = searchResult.selectFirst(".result__snippet");
                
                if (titleElement != null && snippetElement != null) {
                    String title = titleElement.text();
                    String snippet = snippetElement.text();
                    
                    result.append("📄 **").append(title).append("**\n");
                    result.append("   ").append(snippet).append("\n\n");
                    count++;
                }
            }
            
            if (count == 0) {
                return "Could not extract search results. Please try a different query.";
            }
            
            result.append("_Note: For more detailed information, please visit the official websites._");
            
            return result.toString();
        } catch (Exception e) {
            log.error("Error performing web search: {}", e.getMessage(), e);
            return "Unable to perform web search at this time. Error: " + e.getMessage();
        }
    }

    @Tool("Get a summary of available destinations and what we offer. Use this when user asks general questions about destinations or wants an overview.")
    public String getDestinationsSummary() {
        log.info("Tool: Getting destinations summary");
        return """
            🌍 **Our Popular Vietnamese Destinations:**
            
            🏖️ **Da Nang** - Famous for beautiful beaches, Ba Na Hills, Dragon Bridge
            🌊 **Nha Trang** - Coastal paradise with islands and water sports
            🏝️ **Phu Quoc** - Island getaway with pristine beaches and resorts
            🏞️ **Ha Long** - UNESCO World Heritage site with limestone islands
            🏛️ **Hoi An** - Ancient town with rich cultural heritage
            ⛰️ **Sapa** - Mountain retreat with stunning rice terraces
            
            Each destination offers unique hotels, activities, and experiences.
            Would you like to see available hotels or flights for any of these destinations?
            """;
    }
}
