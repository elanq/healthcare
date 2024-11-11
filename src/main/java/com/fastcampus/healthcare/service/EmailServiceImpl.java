package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.config.SendgridConfig;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.entity.DoctorSpecialization;
import com.fastcampus.healthcare.entity.Payment;
import com.fastcampus.healthcare.entity.Specialization;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.model.DoctorResponse;
import com.fastcampus.healthcare.model.PaymentResponse;
import com.fastcampus.healthcare.repository.DoctorSpecializationRepository;
import com.fastcampus.healthcare.repository.PaymentRepository;
import com.fastcampus.healthcare.repository.SpecializationRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final SendgridConfig sendgridConfig;
  private final SendGrid sendGrid;
  private final UserRepository userRepository;
  private final PaymentRepository paymentRepository;
  private final DoctorService doctorService;
  private final DoctorSpecializationRepository doctorSpecializationRepository;
  private final SpecializationRepository specializationRepository;

  @Override
  @Async
  public void notifySuccessfulPayment(Appointment appointment) {
    Optional<User> user = userRepository.findById(appointment.getPatientId());
    if (user.isEmpty()) {
      log.error("User is not exists with id " + appointment.getPatientId() + " during notify successful payment");
      return;
    }

    Optional<Payment> payment = paymentRepository.findByAppointmentId(appointment.getId());
    if (payment.isEmpty()) {
      log.error("Payment is not exists with id " + appointment.getId() + " during notify successful payment");
      return;
    }

    Mail mail = prepareSuccessfulPaymentEmail(user.get(), payment.get());
    try {
      sendEmail(mail);
    } catch (IOException e) {
      log.error("Error while sending email");
    }
  }

  @Override
  @Async
  public void notifyMeetingCreated(Appointment appointment) {
    Optional<User> user = userRepository.findById(appointment.getPatientId());
    if (user.isEmpty()) {
      log.error("User is not exists with id " + appointment.getPatientId() + " during notify meeting created");
      return;
    }

    DoctorResponse doctorResponse = doctorService.getDoctorById(appointment.getDoctorId());

    Optional<DoctorSpecialization> doctorSpecialization = doctorSpecializationRepository.findById(appointment.getDoctorSpecializationId());
    if (doctorSpecialization.isEmpty()) {
      log.error("doctor specialization is not exists is not exists with id " + appointment.getDoctorSpecializationId() + " during notify meeting created");
      return;
    }
    Optional<Specialization> specialization = specializationRepository.findById(doctorSpecialization.get()
        .getSpecializationId());
    if (specialization.isEmpty()) {
      log.error("specialization is not exists is not exists with id " + doctorSpecialization.get()
          .getSpecializationId() + " during notify meeting created");
      return;
    }

    Mail mail = prepareMeetingCreatedEmail(user.get(), appointment, doctorResponse, specialization.get().getName());
    try {
      sendEmail(mail);
    } catch (IOException e) {
      log.error("Error while sending email");
    }
  }

  private Mail prepareSuccessfulPaymentEmail(User user,  Payment payment) {
    Email from = new Email(sendgridConfig.getFromEmail());
    Email toEmail = new Email(user.getEmail());
    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setReplyTo(from);
    mail.setTemplateId(sendgridConfig.getPaymentSuccessTemplateId());

    Personalization personalization = new Personalization();
    personalization.addTo(toEmail);
    personalization.addDynamicTemplateData("customerName", user.getUsername());
    personalization.addDynamicTemplateData("amount", payment.getAmount());
    personalization.addDynamicTemplateData("orderId", payment.getTransactionId());
    personalization.addDynamicTemplateData("paymentDate", payment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

    mail.addPersonalization(personalization);
    return mail;
  }

  private Mail prepareMeetingCreatedEmail(User user, Appointment appointment, DoctorResponse doctorResponse, String specializationName) {
    Email from = new Email(sendgridConfig.getFromEmail());
    Email toEmail = new Email(user.getEmail());
    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setReplyTo(from);
    mail.setTemplateId(sendgridConfig.getMeetingCreatedTemplateId());

    Personalization personalization = new Personalization();
    personalization.addTo(toEmail);
    personalization.addDynamicTemplateData("customerName", user.getUsername());
    personalization.addDynamicTemplateData("doctorName", doctorResponse.getName());
    personalization.addDynamicTemplateData("specializationName", specializationName);
    personalization.addDynamicTemplateData("appointmentDate", appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
    personalization.addDynamicTemplateData("appointmentTime", appointment.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
    personalization.addDynamicTemplateData("meetingUrl", "http://localhost:3000/appointments/"+appointment.getId()+"/meeting");


    mail.addPersonalization(personalization);
    return mail;
  }

  private void sendEmail(Mail mail) throws IOException {
    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    Response response = sendGrid.api(request);
    if (response.getStatusCode() > 299) {
      log.error("Error while sending email. Status code: " + response.getStatusCode());
      throw new IOException("Failed to send email. Status code: " + response.getStatusCode());
    }
  }
}
