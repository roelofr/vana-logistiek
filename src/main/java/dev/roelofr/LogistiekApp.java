package dev.roelofr;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {
        @Tag(name = "Authentication", description = "All actions concerning sessions and users."),
        @Tag(name = "Tickets", description = "Ticket-related operations"),
        @Tag(name = "Districts", description = "District related operations"),
        @Tag(name = "Vendors", description = "Vendor APIs")
    },
    info = @Info(
        title = "Vana Logistiek App",
        version = "${app.version}",
        description = "Een ticket-systeem voor de supporting teams op Castlefest",
        contact = @Contact(
            name = "GitHub Issues",
            url = "http://github.com/roelofr/vana-logistiek/tickets"),
        license = @License(
            name = "GNU General Public License 3.0",
            identifier = "GPL-3.0",
            url = "https://www.gnu.org/licenses/gpl-3.0.html")),
    servers = {
        @Server(
            description = "Localhost",
            url = "http://localhost:8080/"
        ),
        @Server(
            description = "Production",
            url = "https://logistiek.myvana.dev/api/"
        ),
    }
)
public class LogistiekApp extends Application {
    //
}
