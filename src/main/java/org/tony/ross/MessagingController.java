package org.tony.ross;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/api/messaging")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessagingController {

    @Inject
    @Channel("words-out")
    Emitter<String> emitter;

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
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String homepage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Kafka with Quarkus</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
                .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                h1 { color: #333; text-align: center; }
                .form-group { margin: 20px 0; }
                label { display: block; margin-bottom: 5px; font-weight: bold; }
                input[type="text"] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
                button { background: #007cba; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
                button:hover { background: #005a8a; }
                .status { margin-top: 20px; padding: 10px; border-radius: 4px; }
                .success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
                .error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>🚀 Kafka with Quarkus</h1>
                <p>Send messages to your Kafka topic:</p>

                <div class="form-group">
                    <label for="message">Message:</label>
                    <input type="text" id="message" placeholder="Enter your message here...">
                </div>

                <button onclick="sendMessage()">Send to Kafka</button>

                <div id="status"></div>

                <hr style="margin: 30px 0;">
                <h3>API Endpoints:</h3>
                <ul>
                    <li><strong>GET</strong> <code>/api/messaging/health</code> - Check application status</li>
                    <li><strong>POST</strong> <code>/api/messaging/send</code> - Send message to Kafka</li>
                </ul>
            </div>

            <script>
                async function sendMessage() {
                    const message = document.getElementById('message').value;
                    const statusDiv = document.getElementById('status');

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

                        const result = await response.text();

                        if (response.ok) {
                            statusDiv.innerHTML = '<div class="status success">✅ Message sent successfully!</div>';
                            document.getElementById('message').value = '';
                        } else {
                            statusDiv.innerHTML = '<div class="status error">❌ Error: ' + result + '</div>';
                        }
                    } catch (error) {
                        statusDiv.innerHTML = '<div class="status error">❌ Network error: ' + error.message + '</div>';
                    }
                }

                // Allow Enter key to send message
                document.getElementById('message').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        sendMessage();
                    }
                });
            </script>
        </body>
        </html>
        """;
    }
}
