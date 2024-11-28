package hr.fina.student.projekt;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import hr.fina.student.projekt.service.MailService;
@SpringBootApplication
public class ProjektApplication {
	
	//private static final String APPLICATION_NAME = "PROJEKT";
	//private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	public static void main(String[] args) {
		SpringApplication.run(ProjektApplication.class, args);
		// Build a new authorized API client service.
    /*final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

    // Print the labels in the user's account.
    String user = "me";
    ListLabelsResponse listResponse = service.users().labels().list(user).execute();
    List<Label> labels = listResponse.getLabels();
    if (labels.isEmpty()) {
      System.out.println("No labels found.");
    } else {
      System.out.println("Labels:");
      for (Label label : labels) {
        System.out.printf("- %s\n", label.getName());
      }
    }/* */
  }
	}

	
 
