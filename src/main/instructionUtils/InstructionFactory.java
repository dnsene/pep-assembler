package main.instructionUtils;


import java.util.List;
import main.AssemblerException;
import main.InstructionLabel;
import main.Register;
import main.instructionUtils.label.LabelFactory;
import main.variableUtils.Variable;

public class InstructionFactory {
	
	private List<Variable> variablesList;
	private List<Instruction> instructionsList;
	public LabelFactory labelFactory;
	
	public InstructionFactory(List<Variable> varList,List<Instruction> instrList) {
		this.variablesList = varList;
		this.instructionsList = instrList;
		this.labelFactory = new LabelFactory();
		
	}
	
	
	
	public  Instruction getInstruction(String line,int lineNumber) throws AssemblerException {
		
		String lineToProcess = line.trim();
		
		int firstSepPosition = lineToProcess.indexOf(Instruction.LABEL_AND_OPER_SEPARATOR);
		
		if(firstSepPosition==-1){
			
			InstructionLabel iLab = InstructionLabel.getInstructionLabel(lineToProcess);  //A little help for Debugging
			
			if(iLab == null)
				throw new AssemblerException("Syntax Error : No separator between operation and operands OR bad operation syntax ", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
			
			else {
				
				if(iLab==InstructionLabel.LABEL){
					  return this.labelFactory.buildLabel(lineToProcess,"",this.instructionsList.size());
				} 
					
				else{ 
		
					throw new AssemblerException("Syntax Error : Missing Operand ", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
				}
			}
		}
			
		
		String instrWord = lineToProcess.substring(0,firstSepPosition);

		InstructionLabel iLabel = InstructionLabel.getInstructionLabel(instrWord);
		
		if(iLabel == null)
			throw new AssemblerException("Syntax Error : Unknow Operation '"+ instrWord +"' ", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		
		String operandesString = lineToProcess.substring(firstSepPosition).trim();
		
		
		switch (iLabel.getCategorie()) {
			
			case A1: return buildInstCatA1(iLabel, operandesString);
			case A2: return buildInstCatA2(iLabel, operandesString);
			case A3: return buildInstCatA3(iLabel, operandesString);
			case B: return buildInstCatB(iLabel, operandesString);
			case C: return buildInstCatC(iLabel, operandesString);
			case LABEL: return this.labelFactory.buildLabel(instrWord,operandesString,this.instructionsList.size());
			case D: return buildInstCatD(iLabel, operandesString,lineNumber);

		default:
			throw new RuntimeException("Unknow Instruction categorie");
		}	
	}
	
	
	
	
	public  Instruction buildInstCatA1(InstructionLabel iLabel, String operandesString) throws AssemblerException{
		
		String [] operandesTab = processOperands(operandesString,3);
		    
		Register rd = Register.getRegister(operandesTab[0].trim());
		Register rm = Register.getRegister(operandesTab[1].trim());
		String imm5 = operandesTab[2].trim();
		InstructionLabel concreteOperation = iLabel;
		    
		return new InstrCategorieA1(concreteOperation,rd,rm,imm5);
		    
	}
	
	
	
	public Instruction buildInstCatA2(InstructionLabel iLabel, String operandesString) throws AssemblerException{
		
		String [] operandesTab = processOperands(operandesString,3);
		    
		    Register rd = Register.getRegister(operandesTab[0].trim());
		    Register rn = Register.getRegister(operandesTab[1].trim());
		    Register rm = Register.getRegister(operandesTab[2].trim());
		    InstructionLabel concreteOperation = iLabel;
		    
		    return new InstrCategorieA2(concreteOperation, rd, rn, rm);
		       
	}
	
	
	public  Instruction buildInstCatA3(InstructionLabel iLabel, String operandesString) throws AssemblerException{
		
		String [] operandesTab = processOperands(operandesString,2);
		    
		    Register rd = Register.getRegister(operandesTab[0].trim());
		    String imm8 = operandesTab[1].trim();
		    InstructionLabel concreteOperation = iLabel;
		    
		    return new InstrCategorieA3(concreteOperation, rd, imm8);
		   	    
	}
	
	
	public Instruction buildInstCatB(InstructionLabel iLabel, String operandesString) throws AssemblerException{
		
		String [] operandesTab = processOperands(operandesString,2);
		    
		    Register regPos0 = Register.getRegister(operandesTab[0].trim());
		    Register regPos1 = Register.getRegister(operandesTab[1].trim());
		   
		    InstructionLabel concreteOperation = iLabel;
		    
		    return new InstrCategorieB(concreteOperation, regPos0, regPos1);
		      
	}
	
	
	public  Instruction buildInstCatC(InstructionLabel iLabel, String operandesString) throws AssemblerException{
		
		String [] operandesTab = processOperands(operandesString,2);
		    
		    Register rd = Register.getRegister(operandesTab[0].trim());
		    
		    String secondOperand = operandesTab[1].trim();
		    
		    String imm8;
		    
		    if(secondOperand.startsWith("#")){
		    	imm8 = secondOperand;
		    }
		    else{
		    	
		    	Variable variable = this.getVariableFromRam(secondOperand);
		    	
		    	if(variable==null){
		    		throw new AssemblerException("Syntax Error : Using a undeclared variable ", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		    	}
		    	else{
		    		imm8 = variable.getAdressAsImm8();
		    	}
		    }
		
		    
		    InstructionLabel concreteOperation = iLabel;
		    
		    return new InstrCategorieC(concreteOperation, rd, imm8);
		   	    
	}
	
	
	public  Instruction buildInstCatD(InstructionLabel iLabel, String operandesString,int lineNumber) throws AssemblerException{
		
		String [] opTab = operandesString.trim().split("\\s+");
	    
	    if (opTab.length != 1) {
			throw new AssemblerException("Syntax Error : Bad number of operandes" , AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		}
		    
		    InstructionLabel concreteOperation = iLabel;
		    
		    InstrCategorieD ins = new InstrCategorieD(concreteOperation,opTab[0].trim());
		    
		    ins.setLineNumber(lineNumber);
		    
		    return  ins;
		   	    
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private String [] processOperands(String opString, int NbOprandsRequired ) throws AssemblerException{
		
		String [] opTab = opString.split(String.valueOf(Instruction.MULTI_OPER_SEPARATOR));
	    
	    if (opTab.length != NbOprandsRequired) {
			throw new AssemblerException("Syntax Error : Bad number of operandes" , AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		}
	    return opTab;
		
	}
	
	
	
	private Variable getVariableFromRam(String nom){
		
		if(nom==null || nom.isEmpty()) return null;
		
		for (int i = 0; i < variablesList.size(); i++) {
			if ( variablesList.get(i).getNom().compareTo(nom) == 0 ) return variablesList.get(i);
		}
		
		return null;
	}
	
	
	
}
