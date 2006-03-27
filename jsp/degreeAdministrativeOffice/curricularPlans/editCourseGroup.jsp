<%@ taglib uri="/WEB-INF/jsf_core.tld" prefix="f"%>
<%@ taglib uri="/WEB-INF/jsf_tiles.tld" prefix="ft"%>
<%@ taglib uri="/WEB-INF/jsf_fenix_components.tld" prefix="fc"%>
<%@ taglib uri="/WEB-INF/html_basic.tld" prefix="h"%>

<ft:tilesView definition="definition.degreeAdministrativeOffice.masterPage" attributeName="body-inline">
	<f:loadBundle basename="resources/BolonhaManagerResources" var="bolonhaBundle"/>
	
	<h:outputText value="#{CourseGroupManagement.degreeCurricularPlan.name}" style="font-style: italic"/>
	<h:outputFormat value="<h2>#{bolonhaBundle['edit.param']} </h2>" escape="false">
		<f:param value="#{bolonhaBundle['courseGroup']}"/>
	</h:outputFormat>
	<h:messages infoClass="success0" errorClass="error0" layout="table" globalOnly="true"/>
	<h:form>
		<h:outputText escape="false" value="<input id='degreeCurricularPlanID' name='degreeCurricularPlanID' type='hidden' value='#{CourseGroupManagement.degreeCurricularPlanID}'/>"/>
		<h:outputText escape="false" value="<input id='executionYearID' name='executionYearID' type='hidden' value='#{CourseGroupManagement.executionYearID}'/>"/>
		<h:outputText escape="false" value="<input id='courseGroupID' name='courseGroupID' type='hidden' value='#{CourseGroupManagement.courseGroupID}'/>"/>
		<h:outputText escape="false" value="<input id='contextID' name='contextID' type='hidden' value='#{CourseGroupManagement.contextID}'/>"/>
		<h:outputText escape="false" value="<input id='organizeBy' name='organizeBy' type='hidden' value='#{CourseGroupManagement.organizeBy}'/>"/>
		<h:outputText escape="false" value="<input id='showRules' name='showRules' type='hidden' value='#{CourseGroupManagement.showRules}'/>"/>
		<h:outputText escape="false" value="<input id='hideCourses' name='hideCourses' type='hidden' value='#{CourseGroupManagement.hideCourses}'/>"/>
		<h:outputText escape="false" value="<input id='action' name='action' type='hidden' value='#{CourseGroupManagement.action}'/>"/>
		<h:outputText escape="false" value="<input id='toOrder' name='toOrder' type='hidden' value='#{CourseGroupManagement.toOrder}'/>"/>

		<h:outputText value="<div class='simpleblock4'>" escape="false"/>
		<h:outputText value="<fieldset class='lfloat'>" escape="false"/>
		
		<h:outputText value="<p><label>#{bolonhaBundle['name']} (pt):</label>" escape="false"/>
		<h:inputText id="name" required="true" size="60" maxlength="100" value="#{CourseGroupManagement.name}"/>
		<h:message for="name" styleClass="error0"/>
		<h:outputText value="</p>" escape="false"/>
		
		<h:outputText value="<p><label>#{bolonhaBundle['name']} (en):</label>" escape="false"/>
		<h:inputText id="nameEn" required="true" size="60" maxlength="100" value="#{CourseGroupManagement.nameEn}"/>
		<h:message for="nameEn" styleClass="error0"/>
		<h:outputText value="</p>" escape="false"/>
		
		<h:outputText value="<p><label>#{bolonhaBundle['beginExecutionPeriod.validity']}:</label> " escape="false"/>
		<h:selectOneMenu value="#{CourseGroupManagement.beginExecutionPeriodID}">
			<f:selectItems value="#{CourseGroupManagement.beginExecutionPeriodItems}" />
		</h:selectOneMenu>
		<h:outputText value="</p>" escape="false"/>

		<h:outputText value="<p><label>#{bolonhaBundle['endExecutionPeriod.validity']}:</label> " escape="false"/>
		<h:selectOneMenu value="#{CourseGroupManagement.endExecutionPeriodID}">
			<f:selectItems value="#{CourseGroupManagement.endExecutionPeriodItems}" />
		</h:selectOneMenu>
		<h:outputText value="</p>" escape="false"/>

		<h:outputText value="</fieldset></div>" escape="false"/>	
			
		<h:outputText value="<p>" escape="false"/>
		<h:commandButton styleClass="inputbutton" value="#{bolonhaBundle['save']}"
			action="#{CourseGroupManagement.editCourseGroup}"/>
		<h:commandButton immediate="true" styleClass="inputbutton" value="#{bolonhaBundle['cancel']}"
			action="editCurricularPlanStructure"/>	
		<h:outputText value="</p>" escape="false"/>	
	</h:form>
</ft:tilesView>