/*
 * IFrequenta.java
 *
 * Created on 20 de Outubro de 2002, 15:17
 */

package Dominio;

/**
 *
 * @author  tfc130
 */
public interface IFrequenta extends IDomainObject {
	public IStudent getAluno();
	public IDisciplinaExecucao getDisciplinaExecucao();
	public IEnrolment getEnrolment();

	public void setAluno(IStudent aluno);
	public void setDisciplinaExecucao(IDisciplinaExecucao disciplinaExecucao);
	public void setEnrolment(IEnrolment enrolment);

	public Integer getChaveAluno();
	public Integer getChaveDisciplinaExecucao();
	public Integer getKeyEnrolment();
	
	public void setChaveAluno(Integer keyAluno);
	public void setChaveDisciplinaExecucao(Integer keyDisciplinaExecucao);
	public void setKeyEnrolment(Integer keyEnrolment);
}
