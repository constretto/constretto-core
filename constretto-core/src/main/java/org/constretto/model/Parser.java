package org.constretto.model;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public interface Parser {
    CValue parse(String value);
}
