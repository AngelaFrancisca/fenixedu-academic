package net.sourceforge.fenixedu.applicationTier.Servico.person;

import net.sourceforge.fenixedu.dataTransferObject.InfoPerson;
import net.sourceforge.fenixedu.domain.Person;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.FenixFramework;

public class ReadPersonByID {

    @Service
    public static InfoPerson run(String externalId) {
        return InfoPerson.newInfoFromDomain((Person) FenixFramework.getDomainObject(externalId));
    }
}