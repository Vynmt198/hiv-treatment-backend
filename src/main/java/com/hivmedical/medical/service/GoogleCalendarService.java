package com.hivmedical.medical.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.api.client.auth.oauth2.Credential;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "HIV Medical App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        String userId = "nhivyse181985@fpt.edu.vn";
        Credential credential = flow.loadCredential(userId);
        if (credential != null && credential.getAccessToken() != null) {
            return credential;
        }

        // Nếu chưa có token, sinh link xác thực và yêu cầu nhập mã xác thực
        String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
        System.out.println("Please open the following address in your browser:");
        System.out.println("  " + authorizationUrl);
        System.out.println("Enter the authorization code:");

        // Đọc mã xác thực từ console
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();

        // Đổi mã xác thực lấy token
        return flow.createAndStoreCredential(
                flow.newTokenRequest(code).setRedirectUri(redirectUri).execute(), userId);
    }

    public Calendar getCalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Event createOnlineAppointmentEvent(String summary, String description, LocalDateTime startDateTime,
            LocalDateTime endDateTime, String patientEmail) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        Date start = Date.from(startDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        Date end = Date.from(endDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        EventDateTime startEventDateTime = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(start)).setTimeZone("Asia/Ho_Chi_Minh");
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(end))
                .setTimeZone("Asia/Ho_Chi_Minh");
        event.setStart(startEventDateTime);
        event.setEnd(endEventDateTime);

        // Thêm Google Meet
        ConferenceData conferenceData = new ConferenceData();
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
        createConferenceRequest.setRequestId(UUID.randomUUID().toString());
        conferenceData.setCreateRequest(createConferenceRequest);
        event.setConferenceData(conferenceData);

        // Thêm attendee (bệnh nhân)
        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(patientEmail)
        };
        event.setAttendees(Arrays.asList(attendees));

        // Tạo sự kiện
        Calendar.Events.Insert insert = service.events().insert("primary", event)
                .setConferenceDataVersion(1);
        Event createdEvent = insert.execute();

        return createdEvent;
    }
}
