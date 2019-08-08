%dw 2.0
output application/json skipNullOn="everywhere"
fun devOperator (dOperator) = (if (dOperator == 'Health Care Professional')  "10"
	else if (dOperator == 'Patient')  "13"
	else if (dOperator == 'Other')  "21"
	else ""
)
---
{
	(vars.srCaseDetails map ( vcase , indexOfVcase ) -> {
		(vcase.AG_Subcase_Number_Formula_Apex__c) : {
		product:
			if (vcase.Sub_Case_Products__r != null) (vcase.Sub_Case_Products__r map (sCProduct,scPIndex) -> {
				drugcharacterization : "1",
				medicinalproduct: if(sCProduct.AG_Case_Product__r.AG_Product__r != null) sCProduct.AG_Case_Product__r.AG_Product__r.Name else null,
				drugbatchnumb: sCProduct.AG_Case_Product__r.AG_Lot_Batch_Number_NotOnView__c,
				drugdosageform: sCProduct.AG_Case_Product__r.AG_Dosage_Form__r.Name,
				deviceserialnumb_extension: "-",
				"devicedetails_extension": if (vcase.Associated_Cases__r != null) (((((write ((vcase.Associated_Cases__r filter ($.AG_Product__c ==  sCProduct.AG_Case_Product__r.AG_Product__r.Name) map (aCase,aCaseIndex) -> (
					vars.pcmDetails filter ($.AG_Subcase_Number_Formula_Apex__c == aCase.AG_PCM_Sub_Case_Number_Apex__c) map (pcDrug,pcDrugIndex) -> (
					(pcDrug.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r == null or ($.AG_PCM_Medical_Device__r.Name == 'NA' or $.AG_PCM_Medical_Device__r.Name == 'N/A' or $.AG_PCM_Medical_Device__r.Name == '')) map ((Id: "|PCID|" ++ (pcDrug.AG_Subcase_Number_Formula_Apex__c as String) ++ "|Issue ID|" ++ ($.Name as String) ++ (if (pcDrug.AG_Amgen_Notified_Date__c != null) ("|Date of Complaint|" ++ ((pcDrug.AG_Amgen_Notified_Date__c default "") as String)) else "") ++ ( if ($.AG_Risk_Matrix_ID__r.AG_Investigation_Level__c != null) ("|Investigation level|" ++ (($.AG_Risk_Matrix_ID__r.AG_Investigation_Level__c default "") as String)) else "") ++ "|")
					.Id))
					))),"application/java")) replace "[" with "") replace "]" with "") replace "||" with "|") replace ", " with "") else null,
				"drugForDrug": if (vcase.Associated_Cases__r != null) ((vcase.Associated_Cases__r filter ($.AG_Product__c == sCProduct.AG_Case_Product__r.AG_Product__r.Name) map (aCase,aCaseIndex) -> (
					vars.pcmDetails filter ($.AG_Subcase_Number_Formula_Apex__c == aCase.AG_PCM_Sub_Case_Number_Apex__c) map {
					PCMNo: ($.AG_Subcase_Number_Formula_Apex__c default "") ++ (if ($.AG_PCM_Type__r.Name == 'Product Complaint') ('(PC)') else if ($.AG_PCM_Type__r.Name == 'SQI') ('(SQ)') else ""),
					//lotBatchNos:  if ($.PCM_Issues__r != null) ($.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r == null or //($.AG_PCM_Medical_Device__r.Name == 'NA' or $.AG_PCM_Medical_Device__r.Name == '')) map { test: $.Name default "" } else ""
					lotBatchNos: if ($.PCM_Issues__r != null) (flatten (($.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r == null or ($.AG_PCM_Medical_Device__r.Name == 'NA' or $.AG_PCM_Medical_Device__r.Name == 'N/A' or $.AG_PCM_Medical_Device__r.Name == '')) map
					( (vars.relatedLotBatch[$.Name as String]) groupBy($.AG_Lot_Batch_Number__c) pluck ($$))
					)) distinctBy $) else ""
					})[0])  distinctBy $.PCMNo) else "",
				"deviceForDrug": if (vcase.Associated_Cases__r != null) ((vcase.Associated_Cases__r filter ($.AG_Product__c == sCProduct.AG_Case_Product__r.AG_Product__r.Name) map (aCase,aCaseIndex) -> (
					vars.pcmDetails filter ($.AG_Subcase_Number_Formula_Apex__c == aCase.AG_PCM_Sub_Case_Number_Apex__c) map (pcDevice,pcDeviceIndex) -> {
					PCMNo: (pcDevice.AG_Subcase_Number_Formula_Apex__c  default "") ++ (if (pcDevice.AG_PCM_Type__r.Name == 'Product Complaint') ('(PC)') else if (pcDevice.AG_PCM_Type__r.Name == 'SQI') ('(SQ)') else ""),
					PCMIssueDetails: if (pcDevice.PCM_Issues__r != null) (flatten ((pcDevice.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r != null and ($.AG_PCM_Medical_Device__r.Name != 'NA' or $.AG_PCM_Medical_Device__r.Name != 'N/A' or $.AG_PCM_Medical_Device__r.Name != '')) map
					{
					"drugcharacterization" : "1",
					"medicinalproduct": $.AG_PCM_Medical_Device__r.Name,
					"drugbatchnumb": sCProduct.AG_Case_Product__r.AG_Lot_Batch_Number_NotOnView__c,
					"drugdosageform": sCProduct.AG_Case_Product__r.AG_Dosage_Form__r.Name,
					"devicedetails_extension":
					((((write (pcDevice.PCM_Issues__r map (Id: "|PCID|" ++ (pcDevice.AG_Subcase_Number_Formula_Apex__c as String) ++ "|Issue ID|" ++ ($.Name as String) ++ (if (pcDevice.AG_Amgen_Notified_Date__c != null) ("|Date of Complaint|" ++ ((pcDevice.AG_Amgen_Notified_Date__c default "") as String)) else "") ++ ( if ($.AG_Risk_Matrix_ID__r.AG_Investigation_Level__c != null) ("|Investigation level|" ++ (($.AG_Risk_Matrix_ID__r.AG_Investigation_Level__c default "") as String)) else "") ++ "|")
					.Id ,"application/java")) replace "[" with "") replace "]" with "") replace ", " with ""),
					"deviceserialnumb_extension": $.AG_Serial_Number__c,
					(if ( vars.relatedLotBatch[$.Name as String] != null and ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == 'FDP'))) > 0)) ([
					"devmfdgdateformat_extension": "102",
					//"devmfdgdate_extension": (((vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == //'FDP'))[0].AG_Manufacture_Date__c) replace "-" with ""),
					"devmfdgdate_extension":
					(if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))) == 0) null
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))) == 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))[0].AG_Manufacture_Date__c) replace "-" with "")
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == 'FDP' and $.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))) >= 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == 'FDP' and $.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))[0].AG_Manufacture_Date__c) replace "-" with "")
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Primary_Lot_Batch__c == 'true' and $.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))) >= 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Primary_Lot_Batch__c == 'true' and $.AG_Manufacture_Date__c != null and $.AG_Manufacture_Date__c != ''))[0].AG_Manufacture_Date__c) replace "-" with "")
					else null),
					"devlotexpdateformat_extension": "102",
					"devlotexpdate_extension":
					if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))) == 0) null
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))) == 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))[0].AG_Expiration_Date__c) replace "-" with "")
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == 'FDP' and $.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))) >= 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == 'FDP' and $.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))[0].AG_Expiration_Date__c) replace "-" with "")
					else if ((sizeOf(vars.relatedLotBatch[$.Name as String] filter ($.AG_Primary_Lot_Batch__c == 'true' and $.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))) >= 1)
						(((vars.relatedLotBatch[$.Name as String] filter ($.AG_Primary_Lot_Batch__c == 'true' and $.AG_Expiration_Date__c != null and $.AG_Expiration_Date__c != ''))[0].AG_Expiration_Date__c) replace "-" with "")
					else null,
					//"devlotexpdate_extension": (((vars.relatedLotBatch[$.Name as String] filter ($.AG_Manufacturing_Stage__c == //'FDP'))[0].AG_Expiration_Date__c) replace "-" with "")
					]) else ([])),
					(if (pcDevice.AG_User_Experience__c != null) ([
					"devusage_extension": "32",
					"devuseother_extension": pcDevice.AG_User_Experience__c,
					"devuseupd_extension": "0"]) else ([])),
					(if (pcDevice.PCM_Return_Units__r != null) (["devcurloc_extension": (if (pcDevice.PCM_Return_Units__r[0].AG_PCM_Return_Unit_Received_by_Lab_Date__c != null) ((pcDevice.PCM_Return_Units__r[0].AG_PCM_Return_Unit_Received_by_Lab_Date__c) replace "-" with "") else null)]) else ([])),
					"deviceinvolved_extension": if (pcDevice.AG_Number_of_Medical_Devices__c != null) round(pcDevice.AG_Number_of_Medical_Devices__c) else null,
					(if (pcDevice.AG_Operator_of_Medical_Device__c != null) ([
					"devoperator_extension": devOperator(pcDevice.AG_Operator_of_Medical_Device__c),
					"devoperatorother_extension": pcDevice.AG_Operator_of_Medical_Device__c]) else ([])),
					//(if (pcDevice.AG_Usage_Of_Medical_Device_Combination__c != null) ([
					//"deviceusage_extension": pcDevice.AG_Usage_Of_Medical_Device_Combination__c,
					//"deviceuseother_extension": "OTHER"]) else ([])),
					"deviceusage_extension": "32",
					"deviceuseother_extension": "OTHER",
					"deviceremedialaction_extension": "256",
					"deviceremedialactionother_extension": if ((sizeOf(pcDevice.AG_Remedial_Action_Taken_By_HCP__c default "")) <= 160) (pcDevice.AG_Remedial_Action_Taken_By_HCP__c default "OTHER") else "OTHER",
					//"deviceremedialactionother_extension": "OTHER"
					(if ((pcDevice.PCM_Issues__r != null) and (sizeOf(((pcDevice.PCM_Issues__r filter ((vars.CAPAChangeDetails[$.Name as String]) != null) map ((vars.CAPAChangeDetails[$.Name as String]) default "")) joinBy ", ")) > 0)) ([
					"devcorrectiveaction_extension": ((pcDevice.PCM_Issues__r filter ((vars.CAPAChangeDetails[$.Name as String]) != null) map ((vars.CAPAChangeDetails[$.Name as String]) default "")) joinBy ", ")
					]) else ([])),
					//"deviceevaluationmethod_extension": (if ($.AG_Evaluation_Codes_Method__c != null) //(vars.formulaMappingDetails[$.AG_Evaluation_Codes_Method__c as String]) else null),
					//"deviceevaluationresult_extension": (if ($.AG_Evaluation_Codes_Results__c != null) //(vars.formulaMappingDetails[$.AG_Evaluation_Codes_Results__c as String]) else null),
					//"deviceevaluationconclusion_extension": (if ($.AG_Evaluation_Codes_Conclusions__c != null) //(vars.formulaMappingDetails[$.AG_Evaluation_Codes_Conclusions__c as String]) else null),
					"devqcresult_extension": pcDevice.AG_Investigation_Summary_For_Safety__c,
					"devcatalog_extension": $.AG_Catalog__c,
					"deviceudi_extension": $.AG_Unique_Identifier_UDI__c,
					([{((pcDevice.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r != null and ($.AG_PCM_Medical_Device__r.Name != 'NA' or $.AG_PCM_Medical_Device__r.Name != 'N/A' or $.AG_PCM_Medical_Device__r.Name != '')) map {
						(if ( $$ < 4 )([
						( (if ($$ == 0) "deviceevaluationmethod_extension" else ("deviceevalmethcode" ++ ($$ + 1) ++ "_extension"))):
								(if ($.AG_Evaluation_Codes_Method__c != null) (vars.formulaMappingDetails[$.AG_Evaluation_Codes_Method__c as String]) else null)
						]) else  ([]))
					 }
					) map $)}]),
					([{((pcDevice.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r != null and ($.AG_PCM_Medical_Device__r.Name != 'NA' or $.AG_PCM_Medical_Device__r.Name != 'N/A' or $.AG_PCM_Medical_Device__r.Name != '')) map {
						(if ( $$ < 4 )([
						((if ($$ == 0) "deviceevaluationresult_extension" else ("deviceevalrescode" ++ ($$ + 1) ++ "_extension"))):
								(if ($.AG_Evaluation_Codes_Results__c != null) (vars.formulaMappingDetails[$.AG_Evaluation_Codes_Results__c as String]) else null)
						]) else  ([]))
					 }
					) map $)}]),
					([{((pcDevice.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r != null and ($.AG_PCM_Medical_Device__r.Name != 'NA' or $.AG_PCM_Medical_Device__r.Name != 'N/A' or $.AG_PCM_Medical_Device__r.Name != '')) map {
						(if ( $$ < 4 )([
						((if ($$ == 0) "deviceevaluationconclusion_extension" else ("deviceevalconcode" ++ ($$ + 1) ++ "_extension"))):
								(if ($.AG_Evaluation_Codes_Conclusions__c != null) (vars.formulaMappingDetails[$.AG_Evaluation_Codes_Conclusions__c as String]) else null)
						]) else  ([]))
					 }
					) map $)}])
					//++ ( if (pcDevice.AG_Cause_Code_Family__c != null) ("|Cause Code|" ++ ((pcDevice.AG_Cause_Code_Family__c default "") as String)) //else "")
					}
					)))[0] else "",
					lotBatchNos: if (pcDevice.PCM_Issues__r != null) (flatten ((pcDevice.PCM_Issues__r filter ($.AG_PCM_Medical_Device__r != null and ($.AG_PCM_Medical_Device__r.Name != 'NA' or $.AG_PCM_Medical_Device__r.Name != 'N/A' or $.AG_PCM_Medical_Device__r.Name != '')) map
					( (vars.relatedLotBatch[$.Name as String]) groupBy($.AG_Lot_Batch_Number__c) pluck ($$))
					)) distinctBy $) else ""
					})[0])  distinctBy $.PCMNo) else "",
			}) else ""
		}
	})
}