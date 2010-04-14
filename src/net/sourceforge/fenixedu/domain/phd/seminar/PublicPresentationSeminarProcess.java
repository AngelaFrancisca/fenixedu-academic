package net.sourceforge.fenixedu.domain.phd.seminar;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.caseHandling.StartActivity;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.caseHandling.Activity;
import net.sourceforge.fenixedu.domain.caseHandling.PreConditionNotValidException;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramDocumentType;
import net.sourceforge.fenixedu.domain.phd.PhdProgramProcessDocument;
import net.sourceforge.fenixedu.domain.phd.alert.AlertService;
import net.sourceforge.fenixedu.domain.phd.permissions.PhdPermissionType;

public class PublicPresentationSeminarProcess extends PublicPresentationSeminarProcess_Base {

    static abstract private class PhdActivity extends Activity<PublicPresentationSeminarProcess> {

	protected PhdPermissionType getPublicPresentationSeminarPermission() {
	    return PhdPermissionType.PUBLIC_PRESENTATION_SEMINAR_PROCESS_MANAGEMENT;
	}

	@Override
	public void checkPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    processPreConditions(process, userView);
	    activityPreConditions(process, userView);
	}

	protected void processPreConditions(final PublicPresentationSeminarProcess process, final IUserView userView) {
	    if (process != null && process.isExempted()) {
		throw new PreConditionNotValidException();
	    }
	}

	abstract protected void activityPreConditions(final PublicPresentationSeminarProcess process, final IUserView userView);
    }

    @StartActivity
    static public class RequestComission extends PhdActivity {

	@Override
	protected void processPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    // overrided to prevent exempted test
	}

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    // Activity on main process ensures access control
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess noProcess,
		IUserView userView, Object object) {
	    final PublicPresentationSeminarProcess result = new PublicPresentationSeminarProcess();
	    result.createState(PublicPresentationSeminarProcessStateType.WAITING_FOR_COMISSION_CONSTITUTION,
		    userView.getPerson(), ((PublicPresentationSeminarProcessBean) object).getRemarks());

	    return result;
	}
    }

    static public class SubmitComission extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.WAITING_FOR_COMISSION_CONSTITUTION) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView)
		    && !process.getIndividualProgramProcess().isCoordinatorForPhdProgram(userView.getPerson())) {
		throw new PreConditionNotValidException();
	    }
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;
	    bean.getDocument().setType(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_COMISSION);
	    process.addDocument(bean.getDocument(), userView.getPerson());
	    process.createState(PublicPresentationSeminarProcessStateType.COMMISSION_WAITING_FOR_VALIDATION,
		    userView.getPerson(), bean.getRemarks());

	    AlertService.alertAcademicOffice(process.getIndividualProgramProcess(), getPublicPresentationSeminarPermission(),
		    "message.phd.alert.public.presentation.seminar.comission.validation.subject",
		    "message.phd.alert.public.presentation.seminar.comission.validation.body");

	    return process;
	}

    }

    static public class ValidateComission extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {

	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.COMMISSION_WAITING_FOR_VALIDATION) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;
	    bean.getDocument().setType(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_COMISSION);

	    if (bean.getDocument().hasAnyInformation()) {
		process.addDocument(bean.getDocument(), userView.getPerson());
	    }

	    process.createState(PublicPresentationSeminarProcessStateType.COMMISSION_VALIDATED, userView.getPerson(), bean
		    .getRemarks());

	    AlertService.alertGuiders(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.comission.validated.subject",
		    "message.phd.alert.public.presentation.seminar.comission.validated.body");

	    AlertService.alertStudent(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.comission.validated.subject",
		    "message.phd.alert.public.presentation.seminar.comission.validated.body");

	    return process;
	}

    }

    static public class RejectComission extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {

	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.COMMISSION_WAITING_FOR_VALIDATION) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;

	    process.createState(PublicPresentationSeminarProcessStateType.WAITING_FOR_COMISSION_CONSTITUTION, userView
		    .getPerson(), bean.getRemarks());

	    AlertService.alertCoordinator(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.comission.rejected.subject",
		    "message.phd.alert.public.presentation.seminar.comission.rejected.body");

	    return process;
	}

    }

    static public class SchedulePresentationDate extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {

	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.COMMISSION_VALIDATED) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView)
		    && !process.getIndividualProgramProcess().isCoordinatorForPhdProgram(userView.getPerson()))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;
	    process.setPresentationDate(bean.getPresentationDate());
	    process.createState(PublicPresentationSeminarProcessStateType.PUBLIC_PRESENTATION_DATE_SCHEDULED, userView
		    .getPerson(), bean.getRemarks());

	    AlertService.alertAcademicOffice(process.getIndividualProgramProcess(), getPublicPresentationSeminarPermission(),
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.subject",
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.body");

	    AlertService.alertGuiders(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.subject",
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.body");

	    AlertService.alertStudent(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.subject",
		    "message.phd.alert.public.presentation.seminar.scheduled.presentation.date.body");

	    return process;
	}
    }

    static public class UploadReport extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.PUBLIC_PRESENTATION_DATE_SCHEDULED) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView)
		    && !process.getIndividualProgramProcess().isGuider(userView.getPerson()))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;
	    bean.getDocument().setType(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_REPORT);
	    process.addDocument(bean.getDocument(), userView.getPerson());
	    process.createState(PublicPresentationSeminarProcessStateType.REPORT_WAITING_FOR_VALIDATION, userView.getPerson(),
		    bean.getRemarks());

	    AlertService.alertAcademicOffice(process.getIndividualProgramProcess(), getPublicPresentationSeminarPermission(),
		    "message.phd.alert.public.presentation.seminar.report.uploaded.subject",
		    "message.phd.alert.public.presentation.seminar.report.uploaded.body");

	    return process;
	}
    }

    static public class ValidateReport extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.REPORT_WAITING_FOR_VALIDATION) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;
	    bean.getDocument().setType(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_REPORT);

	    if (bean.getDocument().hasAnyInformation()) {
		process.addDocument(bean.getDocument(), userView.getPerson());
	    }

	    process.createState(PublicPresentationSeminarProcessStateType.REPORT_VALIDATED, userView.getPerson(), bean
		    .getRemarks());

	    AlertService.alertCoordinator(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.report.validated.subject",
		    "message.phd.alert.public.presentation.seminar.report.validated.body");

	    AlertService.alertGuiders(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.report.validated.subject",
		    "message.phd.alert.public.presentation.seminar.report.validated.body");

	    AlertService.alertStudent(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.report.validated.subject",
		    "message.phd.alert.public.presentation.seminar.report.validated.body");

	    return process;
	}
    }

    static public class RejectReport extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    if (process.getActiveState() != PublicPresentationSeminarProcessStateType.REPORT_WAITING_FOR_VALIDATION) {
		throw new PreConditionNotValidException();
	    }

	    if (!isMasterDegreeAdministrativeOfficeEmployee(userView))
		throw new PreConditionNotValidException();
	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {

	    final PublicPresentationSeminarProcessBean bean = (PublicPresentationSeminarProcessBean) object;

	    process.createState(PublicPresentationSeminarProcessStateType.PUBLIC_PRESENTATION_DATE_SCHEDULED, userView
		    .getPerson(), bean.getRemarks());

	    AlertService.alertGuiders(process.getIndividualProgramProcess(),
		    "message.phd.alert.public.presentation.seminar.report.rejected.subject",
		    "message.phd.alert.public.presentation.seminar.report.rejected.body");

	    return process;
	}
    }

    static public class DownloadReportDocument extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {

	    if (!process.hasReportDocument()) {
		throw new PreConditionNotValidException();
	    }

	    if (isMasterDegreeAdministrativeOfficeEmployee(userView)
		    || process.getIndividualProgramProcess().isCoordinatorForPhdProgram(userView.getPerson())
		    || process.getIndividualProgramProcess().isGuiderOrAssistentGuider(userView.getPerson())) {
		return;
	    }

	    if (process.getActiveState() == PublicPresentationSeminarProcessStateType.REPORT_VALIDATED
		    && process.getIndividualProgramProcess().getPerson() == userView.getPerson()) {
		return;
	    }

	    throw new PreConditionNotValidException();

	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {
	    // Nothing to be done
	    return null;
	}

    }

    static public class DownloadComissionDocument extends PhdActivity {

	@Override
	protected void activityPreConditions(PublicPresentationSeminarProcess process, IUserView userView) {
	    if (!process.hasComissionDocument()) {
		throw new PreConditionNotValidException();
	    }

	    if (isMasterDegreeAdministrativeOfficeEmployee(userView)
		    || process.getIndividualProgramProcess().isCoordinatorForPhdProgram(userView.getPerson())
		    || process.getIndividualProgramProcess().isGuiderOrAssistentGuider(userView.getPerson())) {
		return;
	    }

	    if (process.hasState(PublicPresentationSeminarProcessStateType.COMMISSION_VALIDATED)
		    && process.getIndividualProgramProcess().getPerson() == userView.getPerson()) {
		return;
	    }

	    throw new PreConditionNotValidException();

	}

	@Override
	protected PublicPresentationSeminarProcess executeActivity(PublicPresentationSeminarProcess process, IUserView userView,
		Object object) {
	    // Nothing to be done

	    return null;
	}

    }

    static private List<Activity> activities = new ArrayList<Activity>();

    static {
	activities.add(new SubmitComission());
	activities.add(new ValidateComission());
	activities.add(new RejectComission());
	activities.add(new SchedulePresentationDate());
	activities.add(new UploadReport());
	activities.add(new ValidateReport());
	activities.add(new RejectReport());
	activities.add(new DownloadReportDocument());
	activities.add(new DownloadComissionDocument());
    }

    private PublicPresentationSeminarProcess() {
	super();
    }

    public boolean hasReportDocument() {
	return getReportDocument() != null;
    }

    public PhdProgramProcessDocument getReportDocument() {
	return getLastestDocumentVersionFor(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_REPORT);
    }

    public boolean hasComissionDocument() {
	return getComissionDocument() != null;
    }

    public PhdProgramProcessDocument getComissionDocument() {
	return getLastestDocumentVersionFor(PhdIndividualProgramDocumentType.PUBLIC_PRESENTATION_SEMINAR_COMISSION);
    }

    @Override
    public String getDisplayName() {
	return ResourceBundle.getBundle("resources/PhdResources").getString(getClass().getSimpleName());
    }

    @Override
    public boolean canExecuteActivity(IUserView userView) {
	return false;
    }

    @Override
    public List<Activity> getActivities() {
	return activities;
    }

    @Override
    public PublicPresentationSeminarState getMostRecentState() {
	return (PublicPresentationSeminarState) super.getMostRecentState();
    }

    @Override
    public PublicPresentationSeminarProcessStateType getActiveState() {
	return (PublicPresentationSeminarProcessStateType) super.getActiveState();
    }

    public void createState(final PublicPresentationSeminarProcessStateType type, final Person person, final String remarks) {
	new PublicPresentationSeminarState(this, type, person, remarks);
    }

    @Override
    protected Person getPerson() {
	return getIndividualProgramProcess().getPerson();
    }

    public boolean isExempted() {
	return getActiveState() == PublicPresentationSeminarProcessStateType.EXEMPTED;
    }

    public boolean isConcluded() {
	return getActiveState() == PublicPresentationSeminarProcessStateType.REPORT_VALIDATED;
    }

}
