package org.tony.ross;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.tony.ross.entity.Actor;
import org.tony.ross.repository.ActorRepository;
import java.util.List;

@Path("/api/messaging")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessagingController {

    @Inject
    @Channel("words-out")
    Emitter<String> emitter;

    @Inject
    ActorRepository actorRepository;

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("{\"status\": \"Kafka messaging application is running\", \"timestamp\": \"" +
                          System.currentTimeMillis() + "\"}").build();
    }

    @POST
    @Path("/send")
    public Response sendMessage(String message) {
        try {
            emitter.send(message);
            return Response.ok("{\"result\": \"SUCCESS\", \"message\": \"Message sent to Kafka topic\", \"data\": \"" +
                              message + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"result\": \"ERROR\", \"message\": \"" + e.getMessage() + "\", \"data\": null}")
                          .build();
        }
    }

    @GET
    @Path("/actors")
    public Response getActors() {
        try {
            List<Actor> actors = actorRepository.findTop10();
            return Response.ok(actors).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"result\": \"ERROR\", \"message\": \"Database error: " + e.getMessage() + "\", \"data\": null}")
                          .build();
        }
    }

    @GET
    @Path("/actors/search")
    public Response searchActors(@QueryParam("firstName") String firstName,
                                @QueryParam("lastName") String lastName) {
        try {
            List<Actor> actors;
            if (firstName != null && lastName != null) {
                actors = actorRepository.findByFullName(firstName, lastName);
            } else if (firstName != null) {
                actors = actorRepository.findByFirstName(firstName);
            } else if (lastName != null) {
                actors = actorRepository.findByLastName(lastName);
            } else {
                actors = actorRepository.findTop10();
            }
            return Response.ok(actors).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"result\": \"ERROR\", \"message\": \"Database search error: " + e.getMessage() + "\", \"data\": null}")
                          .build();
        }
    }

    @GET
    @Path("/db/health")
    public Response databaseHealth() {
        try {
            long count = actorRepository.count();
            return Response.ok("{\"status\": \"Database connected\", \"actorCount\": " + count + ", \"timestamp\": \"" +
                              System.currentTimeMillis() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"result\": \"ERROR\", \"message\": \"Database connection failed: " + e.getMessage() + "\", \"data\": null}")
                          .build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String homepage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Kafka with Quarkus + MySQL</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
                .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                h1 { color: #333; text-align: center; }
                h2 { color: #007cba; border-bottom: 2px solid #007cba; padding-bottom: 10px; }
                .form-group { margin: 20px 0; }
                label { display: block; margin-bottom: 5px; font-weight: bold; }
                input[type="text"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
                button { background: #007cba; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }
                button:hover { background: #005a8a; }
                .status { margin-top: 20px; padding: 10px; border-radius: 4px; }
                .success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
                .error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
                .api-section { margin: 30px 0; }
                .actors-list { max-height: 300px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; border-radius: 4px; }
                .actor-item { padding: 5px; border-bottom: 1px solid #eee; }
                .search-form { display: flex; gap: 10px; align-items: end; }
                .search-form input { flex: 1; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>🚀 Kafka with Quarkus + MySQL Sakila</h1>

                <h2>📨 Kafka Messaging</h2>
                <p>Send messages to your Kafka topic:</p>

                <div class="form-group">
                    <label for="message">Message:</label>
                    <input type="text" id="message" placeholder="Enter your message here...">
                </div>

                <button onclick="sendMessage()">Send to Kafka</button>

                <div id="kafkaStatus"></div>

                <h2>🎭 Sakila Database</h2>
                <p>Search actors in the Sakila database:</p>

                <div class="search-form">
                    <input type="text" id="firstName" placeholder="First Name">
                    <input type="text" id="lastName" placeholder="Last Name">
                    <button onclick="searchActors()">Search Actors</button>
                    <button onclick="loadTop10()">Load Top 10</button>
                    <button onclick="checkDatabase()">Check DB Health</button>
                </div>

                <div id="dbStatus"></div>
                <div id="actorsList" class="actors-list"></div>

                <hr style="margin: 30px 0;">
                <h3>API Endpoints:</h3>
                <div class="api-section">
                    <h4>Kafka Messaging:</h4>
                    <ul>
                        <li><strong>GET</strong> <code>/api/messaging/health</code> - Check application status</li>
                        <li><strong>POST</strong> <code>/api/messaging/send</code> - Send message to Kafka</li>
                    </ul>
                    <h4>Database:</h4>
                    <ul>
                        <li><strong>GET</strong> <code>/api/messaging/db/health</code> - Check database connection</li>
                        <li><strong>GET</strong> <code>/api/messaging/actors</code> - Get top 10 actors</li>
                        <li><strong>GET</strong> <code>/api/messaging/actors/search?firstName=John&lastName=Doe</code> - Search actors</li>
                    </ul>
                </div>
            </div>

            <script>
                async function sendMessage() {
                    const message = document.getElementById('message').value;
                    const statusDiv = document.getElementById('kafkaStatus');

                    if (!message.trim()) {
                        statusDiv.innerHTML = '<div class="status error">Please enter a message</div>';
                        return;
                    }

                    try {
                        const response = await fetch('/api/messaging/send', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: message
                        });

                        if (response.ok) {
                            statusDiv.innerHTML = '<div class="status success">✅ Message sent successfully!</div>';
                            document.getElementById('message').value = '';
                        } else {
                            const error = await response.text();
                            statusDiv.innerHTML = '<div class="status error">❌ Error: ' + error + '</div>';
                        }
                    } catch (error) {
                        statusDiv.innerHTML = '<div class="status error">❌ Network error: ' + error.message + '</div>';
                    }
                }

                async function searchActors() {
                    const firstName = document.getElementById('firstName').value;
                    const lastName = document.getElementById('lastName').value;
                    const statusDiv = document.getElementById('dbStatus');
                    const actorsList = document.getElementById('actorsList');

                    let url = '/api/messaging/actors/search';
                    const params = new URLSearchParams();
                    if (firstName) params.append('firstName', firstName);
                    if (lastName) params.append('lastName', lastName);
                    if (params.toString()) url += '?' + params.toString();

                    try {
                        const response = await fetch(url);
                        const actors = await response.json();

                        if (response.ok) {
                            statusDiv.innerHTML = '<div class="status success">✅ Found ' + actors.length + ' actors</div>';
                            displayActors(actors);
                        } else {
                            statusDiv.innerHTML = '<div class="status error">❌ Error: ' + actors.message + '</div>';
                        }
                    } catch (error) {
                        statusDiv.innerHTML = '<div class="status error">❌ Network error: ' + error.message + '</div>';
                    }
                }

                async function loadTop10() {
                    const statusDiv = document.getElementById('dbStatus');
                    const actorsList = document.getElementById('actorsList');

                    try {
                        const response = await fetch('/api/messaging/actors');
                        const actors = await response.json();

                        if (response.ok) {
                            statusDiv.innerHTML = '<div class="status success">✅ Loaded top 10 actors</div>';
                            displayActors(actors);
                        } else {
                            statusDiv.innerHTML = '<div class="status error">❌ Error: ' + actors.message + '</div>';
                        }
                    } catch (error) {
                        statusDiv.innerHTML = '<div class="status error">❌ Network error: ' + error.message + '</div>';
                    }
                }

                async function checkDatabase() {
                    const statusDiv = document.getElementById('dbStatus');

                    try {
                        const response = await fetch('/api/messaging/db/health');
                        const result = await response.json();

                        if (response.ok) {
                            statusDiv.innerHTML = '<div class="status success">✅ Database connected - Total actors: ' + result.actorCount + '</div>';
                        } else {
                            statusDiv.innerHTML = '<div class="status error">❌ Database error: ' + result.message + '</div>';
                        }
                    } catch (error) {
                        statusDiv.innerHTML = '<div class="status error">❌ Network error: ' + error.message + '</div>';
                    }
                }

                function displayActors(actors) {
                    const actorsList = document.getElementById('actorsList');
                    if (actors.length === 0) {
                        actorsList.innerHTML = '<div class="actor-item">No actors found</div>';
                        return;
                    }

                    const html = actors.map(actor =>
                        '<div class="actor-item">' +
                        '<strong>' + actor.firstName + ' ' + actor.lastName + '</strong> (ID: ' + actor.actorId + ')' +
                        '</div>'
                    ).join('');

                    actorsList.innerHTML = html;
                }

                // Allow Enter key to send message
                document.getElementById('message').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        sendMessage();
                    }
                });

                // Allow Enter key to search actors
                document.getElementById('firstName').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        searchActors();
                    }
                });

                document.getElementById('lastName').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        searchActors();
                    }
                });
            </script>
        </body>
        </html>
        """;
    }
}
