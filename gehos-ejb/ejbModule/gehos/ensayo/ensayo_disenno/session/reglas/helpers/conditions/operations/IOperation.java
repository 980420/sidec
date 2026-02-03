package gehos.ensayo.ensayo_disenno.session.reglas.helpers.conditions.operations;

public interface IOperation 
{
	String symbol();
	String name();
	String[] types();	
	
	static IOperation[] operations = new IOperation[]{
		new PlusOperation(), new MinusOperation(), new DivideOperation(), new MultiplyOperation()
	};
}
