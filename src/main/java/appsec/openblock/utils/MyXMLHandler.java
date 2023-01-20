package appsec.openblock.utils;

import appsec.openblock.DTO.Bid;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyXMLHandler extends DefaultHandler {

    private Bid bid;
    private StringBuilder data = null;
    boolean nonce = false;
    boolean bid_id = false;
    boolean uid = false;
    boolean id = false;

    public Bid getBidObject() {
        return bid;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("bid")) {
            bid = new Bid();
        } else if (qName.equalsIgnoreCase("id")) {
            id = true;
        } else if (qName.equalsIgnoreCase("price")) {
            bid_id = true;
        } else if (qName.equalsIgnoreCase("uid")) {
            uid = true;
        } else if (qName.equalsIgnoreCase("nonce")) {
            nonce = true;
        }
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (bid_id) {
            bid.setPrice(Double.parseDouble(data.toString()));
            bid_id = false;
        } else if (id) {
            bid.setId(Long.parseLong(data.toString()));
            id = false;
        } else if (uid) {
            bid.setUid(Long.parseLong(data.toString()));
            uid = false;
        } else if (nonce) {
            bid.setNonce(data.toString());
            nonce = false;
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}