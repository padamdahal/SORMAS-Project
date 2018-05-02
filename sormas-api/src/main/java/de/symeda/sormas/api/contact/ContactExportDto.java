package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ContactExportDto implements Serializable {

	private static final long serialVersionUID = 2054231712903661096L;

	public static final String I18N_PREFIX = "ContactExport";
	
	private String uuid;
	private String sourceCaseUuid;
	private CaseClassification caseClassification;
	private String disease;
	private ContactClassification contactClassification;
	private Date lastContactDate;
	private String person;
	private Sex sex;
	private String approximateAge;
	private Date reportDate;
	private ContactProximity contactProximity;
	private ContactStatus contactStatus;
	private PresentCondition presentCondition;
	private Date deathDate;
	private String address;
	private String phone;
	private String occupationType;
	private int numberOfVisits;
	private YesNoUnknown symptomatic;
	private Date lastVisitDate;
	private String lastSymptoms;


	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
	}

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	@Order(1)
	public String getSourceCaseUuid() {
		return sourceCaseUuid;
	}

	@Order(2)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(3)
	public String getDisease() {
		return disease;
	}

	@Order(4)
	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	@Order(5)
	public Date getLastContactDate() {
		return lastContactDate;
	}

	@Order(6)
	public String getPerson() {
		return person;
	}

	@Order(7)
	public Sex getSex() {
		return sex;
	}

	@Order(8)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(9)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(10)
	public ContactProximity getContactProximity() {
		return contactProximity;
	}

	@Order(11)
	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	@Order(12)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(13)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(14)
	public String getAddress() {
		return address;
	}

	@Order(15)
	public String getPhone() {
		return phone;
	}

	@Order(16)
	public String getOccupationType() {
		return occupationType;
	}

	@Order(17)
	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	@Order(18)
	public YesNoUnknown getSymptomatic() {
		return symptomatic;
	}

	@Order(19)
	public Date getLastVisitDate() {
		return lastVisitDate;
	}

	@Order(20)
	public String getLastSymptoms() {
		return lastSymptoms;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public void setSourceCaseUuid(String sourceCaseUuid) {
		this.sourceCaseUuid = sourceCaseUuid;
	}


	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}


	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}


	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public void setPerson(String person) {
		this.person = person;
	}


	public void setSex(Sex sex) {
		this.sex = sex;
	}


	public void setApproximateAge(String approximateAge) {
		this.approximateAge = approximateAge;
	}


	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}


	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}


	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}


	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}


	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}


	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}


	public void setSymptomatic(YesNoUnknown symptomatic) {
		this.symptomatic = symptomatic;
	}


	public void setLastVisitDate(Date lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	public void setLastSymptoms(String lastSymptoms) {
		this.lastSymptoms = lastSymptoms;
	}
}