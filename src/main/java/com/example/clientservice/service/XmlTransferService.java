package com.example.clientservice.service;

import com.example.clientservice.dto.ClientDto;
import com.example.clientservice.xml.ClientType;
import com.example.clientservice.xml.Clients;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Service
public class XmlTransferService {
    private final ClientService clientService;

    public XmlTransferService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Transactional
    public int importXml(String xml) {
        try {
            Clients clients = parse(xml);
            int count = 0;
            for (ClientType c : clients.getClient()) {
                clientService.createOrUpdateByPassport(toDto(c));
                count++;
            }
            return count;
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось импортировать XML: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ClientDto createClientXml(String xml) {
        try {
            ClientType client = getSingleClient(parse(xml));
            return clientService.create(toDto(client));
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось создать клиента из XML: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ClientDto updateClientXml(Long id, String xml) {
        try {
            ClientType client = getSingleClient(parse(xml));
            return clientService.update(id, toDto(client));
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось обновить клиента из XML: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public String exportClientXml(Long id) {
        return marshal(List.of(clientService.findById(id)));
    }

    private Clients parse(String xml) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Clients.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try (InputStream xsd = new ClassPathResource("xsd/clients.xsd").getInputStream()) {
            schema = sf.newSchema(new javax.xml.transform.stream.StreamSource(xsd));
        }
        unmarshaller.setSchema(schema);
        return (Clients) unmarshaller.unmarshal(new StringReader(xml));
    }

    private ClientType getSingleClient(Clients clients) {
        if (clients.getClient().size() != 1)
            throw new IllegalArgumentException("XML должен содержать ровно одного клиента");
        return clients.getClient().get(0);
    }

    private ClientDto toDto(ClientType c) {
        return new ClientDto(null, c.getLastName(), c.getFirstName(), c.getMiddleName(),
                c.getBirthDate().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                c.getPassportSeries(), c.getPassportNumber(), c.getCity(), c.getResidenceAddress(),
                c.getMobilePhone(), c.getEmail(), c.isEmployed(), c.getPosition(),
                c.getRegistrationAddress(), c.getCitizenship());
    }

    @Transactional(readOnly = true)
    public String exportXml() {
        return marshal(clientService.findAll());
    }

    private String marshal(List<ClientDto> values) {
        try {
            Clients root = new Clients();
            for (ClientDto d : values) {
                ClientType c = new ClientType();
                c.setLastName(d.lastName()); c.setFirstName(d.firstName()); c.setMiddleName(d.middleName());
                c.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(d.birthDate().toString()));
                c.setPassportSeries(d.passportSeries()); c.setPassportNumber(d.passportNumber());
                c.setCity(d.city()); c.setResidenceAddress(d.residenceAddress());
                c.setMobilePhone(d.mobilePhone()); c.setEmail(d.email()); c.setEmployed(d.employed());
                c.setPosition(d.position()); c.setRegistrationAddress(d.registrationAddress());
                c.setCitizenship(d.citizenship());
                root.getClient().add(c);
            }
            JAXBContext context = JAXBContext.newInstance(Clients.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            marshaller.marshal(root, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сформировать XML: " + e.getMessage(), e);
        }
    }
}