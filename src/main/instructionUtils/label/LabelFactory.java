package main.instructionUtils.label;

import java.util.ArrayList;
import java.util.List;

import main.AssemblerException;


public class LabelFactory {
	
	private List<Label> labelsList;
	
	
	public LabelFactory() {
		this.labelsList = new ArrayList<>();
	}




	public Label buildLabel(String labelNameWithDeuxpoints, String operandesString, int adress) throws AssemblerException{
		
		String labelName =labelNameWithDeuxpoints.substring(0,labelNameWithDeuxpoints.length()-1); 
		

		if(! operandesString.trim().isEmpty()){
			
			throw new AssemblerException("Syntax Error : Label Name should be single in a line ", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		}
		
		Label lab =  new Label(labelName.trim(),adress);
		this.addLabeltoList(lab);
		return lab;
		      
	}
	
	
	
	
	private void addLabeltoList(Label label) throws AssemblerException{
		
		if( this.getLabelFromName(label.getName())!=null)
			throw new AssemblerException("Syntax Error : Duplicated Declaration for Label ' "+label.getName()+" '", AssemblerException.ERR_LAUNCHER_BFCK_RUNTIME_FAILED);
		
		this.labelsList.add(label);
	}
	
	
	
	
	public Label getLabelFromName(String nom) {
		
		for (int i = 0; i < this.labelsList.size(); i++) {
			if ( this.labelsList.get(i).getName().compareToIgnoreCase(nom) == 0 )  return labelsList.get(i);	
		}
		
		return null;
	}
	
}
