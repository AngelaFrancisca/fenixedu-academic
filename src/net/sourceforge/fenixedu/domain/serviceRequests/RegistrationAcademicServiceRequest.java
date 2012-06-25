package net.sourceforge.fenixedu.domain.serviceRequests;

import java.util.Comparator;

import net.sourceforge.fenixedu.dataTransferObject.serviceRequests.RegistrationAcademicServiceRequestCreateBean;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.Employee;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.space.Campus;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.student.Student;
import net.sourceforge.fenixedu.injectionCode.AccessControl;

abstract public class RegistrationAcademicServiceRequest extends RegistrationAcademicServiceRequest_Base {

    public static Comparator<RegistrationAcademicServiceRequest> COMPARATOR_BY_SERVICE_REQUEST_NUMBER_AND_ID = new Comparator<RegistrationAcademicServiceRequest>() {
	public int compare(RegistrationAcademicServiceRequest o1, RegistrationAcademicServiceRequest o2) {
	    if (o1.getServiceRequestNumber().compareTo(o2.getServiceRequestNumber()) != 0) {
		return o1.getServiceRequestNumber().compareTo(o2.getServiceRequestNumber());
	    }
	    return COMPARATOR_BY_ID.compare(o1, o2);
	}
    };

    protected RegistrationAcademicServiceRequest() {
	super();
    }

    public void init(final RegistrationAcademicServiceRequestCreateBean bean) {
	checkParameters(bean);
	super.setRegistration(bean.getRegistration());
	super.init(bean);
    }

    private void checkParameters(final RegistrationAcademicServiceRequestCreateBean bean) {
	checkRegistration(bean);
	checkRegistrationIsNotTransited(bean);
	checkRegistrationStartDate(bean);
	checkRegistrationExecutionYear(bean);
    }

    protected void checkRegistrationExecutionYear(RegistrationAcademicServiceRequestCreateBean bean) {
	if (bean.getExecutionYear() != null && bean.getExecutionYear().isBefore(bean.getRegistration().getStartExecutionYear())) {
	    throw new DomainException("error.RegistrationAcademicServiceRequest.executionYear.before.registrationStartDate");
	}
    }

    protected void checkRegistrationStartDate(RegistrationAcademicServiceRequestCreateBean bean) {
	if (ExecutionYear.readByDateTime(bean.getRequestDate()).isBefore(bean.getRegistration().getStartExecutionYear())) {
	    throw new DomainException("error.RegistrationAcademicServiceRequest.requestDate.before.registrationStartDate");
	}
    }

    protected void checkRegistrationIsNotTransited(RegistrationAcademicServiceRequestCreateBean bean) {
	if (!isAvailableForTransitedRegistrations() && bean.getRegistration().isTransited()) {
	    throw new DomainException("RegistrationAcademicServiceRequest.registration.cannot.be.transited");
	}
    }

    protected void checkRegistration(final RegistrationAcademicServiceRequestCreateBean bean) {
	if (bean.getRegistration() == null) {
	    throw new DomainException("error.serviceRequests.AcademicServiceRequest.registration.cannot.be.null");
	}
    }

    @Override
    protected AdministrativeOffice findAdministrativeOffice() {
	AdministrativeOffice administrativeOffice = super.findAdministrativeOffice();
	if (administrativeOffice == null) {
	    administrativeOffice = AdministrativeOffice.getResponsibleAdministrativeOffice(getRegistration().getDegree());
	}
	return administrativeOffice;
    }

    @Override
    public void setRegistration(Registration registration) {
	throw new DomainException("error.serviceRequests.RegistrationAcademicServiceRequest.cannot.modify.registration");
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
	final ExecutionYear executionYear = hasExecutionYear() ? getExecutionYear() : ExecutionYear
		.readByDateTime(getRequestDate());
	return getRegistration().getStudentCurricularPlan(executionYear);
    }

    public Degree getDegree() {
	return getStudentCurricularPlan().getDegree();
    }

    public DegreeType getDegreeType() {
	return getDegree().getDegreeType();
    }

    public boolean isBolonha() {
	return getDegree().isBolonhaDegree();
    }

    public Campus getCampus() {
	final StudentCurricularPlan studentCurricularPlan = getStudentCurricularPlan();
	return studentCurricularPlan != null ? studentCurricularPlan.getCurrentCampus() : null;
    }

    @Override
    public boolean isAvailableForEmployeeToActUpon() {
	final Person loggedPerson = AccessControl.getPerson();
	if (loggedPerson.hasEmployee()) {
	    final Employee employee = loggedPerson.getEmployee();
	    return employee.getAdministrativeOffice() == getAdministrativeOffice() && employee.getCurrentCampus() == getCampus();
	} else {
	    throw new DomainException("RegistrationAcademicServiceRequest.non.employee.person.attempt.to.change.request");
	}
    }

    @Override
    public boolean isRequestForRegistration() {
	return true;
    }

    @Override
    protected void disconnect() {
	super.setRegistration(null);
	super.disconnect();
    }

    @Override
    public Person getPerson() {
	return getRegistration().getPerson();
    }

    public Student getStudent() {
	return getRegistration().getStudent();
    }

    abstract public boolean isAvailableForTransitedRegistrations();

}
