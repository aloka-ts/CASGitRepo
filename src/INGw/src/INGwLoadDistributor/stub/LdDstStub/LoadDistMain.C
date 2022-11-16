#include <iostream.h>

#include <Util/imOid.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwLoadDistributor/INGwLdDstMgr.h>

#include <string>

using namespace std;

string ss7AppInfo = "1-111-5,240,1,ITU92,60000,150001,15000|1-111-6,241,2,ITU92,60000,150001,15000"; 

main()
{
	INGwIfrPrParamRepository::getInstance().setValue(ingwSS7_APP_INFO, ss7AppInfo);
	INGwIfrPrParamRepository::getInstance().setValue(ingwLOAD_DIST_PATTERN, "ROUND_ROBIN");

	INGwLdDstMgr *mgr = NULL;

	mgr = new INGwLdDstMgr;
	int retVal = mgr->initialize();

	if (G_SUCCESS != retVal) {
		printf("INGwLdDstMgr Initialization Failed......exiting\n");
		exit (0);
	}

	int choice = 1;
	while (choice) {
		printf("\nsetOpcSsnRegisteredWithStack()  - 1\n");
		printf("unsetOpcSsnRegisteredWithStack() - 2\n");
		printf("getNumOfSasAppReg()              - 3\n");
		printf("getOpcSsnList()                  - 4\n");
		printf("isOpcSsnRegisteredWithStack()    - 5\n");
		printf("registerSASApp()                 - 6\n");
		printf("deRegisterSASApp()               - 7\n");
		printf("getDestSasInfo()                 - 8\n");
		printf("debugLoadDistInfo()              - 9\n");
		printf("removeSasApp()                   - 10\n");
		printf("getAllRegSasApp()                - 11\n");
		printf("exit                             - 0\n");
		scanf("%d", &choice);

		switch (choice) {
			case 0: {
				exit (0);
				break;
			}

			case 1: {
				printf("\n********setOpcSsnRegisteredWithStack**************\n");
				mgr->setOpcSsnRegisteredWithStack(2941, 240);
				printf("\n********setOpcSsnRegisteredWithStack-End**************\n");

				break;
			}

			case 2: {
				printf("\n********UnsetOpcSsnRegisteredWithStack**************\n");
				mgr->unsetOpcSsnRegisteredWithStack(2941, 240);
				printf("\n********UnsetOpcSsnRegisteredWithStack-End**************\n");
				break;
			}

			case 3: {
				printf("\n********UnsetOpcSsnRegisteredWithStack**************\n");
				int retVal = mgr->getNumOfSasAppReg(2941, 240);

				printf("Number of registered application [%d]\n", retVal);
				printf("\n********UnsetOpcSsnRegisteredWithStack-End**************\n");
				break;
			}

			case 4: {
				printf("\n********getOpcSsnList**************\n");
				INGwLdPcSsnList retVal = mgr->getOpcSsnList();

				printf("OPC-SSN LIST\n");

				for (int i=0; i < retVal.size(); ++i) {
					printf("OPC [%d-%d-%d] ssn[%d] \n",
						retVal[i].m_pcDetail[0], retVal[i].m_pcDetail[1],
						retVal[i].m_pcDetail[2], retVal[i].m_ssn);
				}
				printf("\n********getOpcSsnList-End**************\n");
				break;
			}

			case 5: {
				printf("\n********isOpcSsnRegisteredWithStack**************\n");
				bool retVal = mgr->isOpcSsnRegisteredWithStack(2941, 240);

				if (true == retVal) {
					printf("OPC[%d] SSn[%d] is %s registered\n", 2941, 240, 
					(retVal == true)?" ":"not");
				}

				retVal = mgr->isOpcSsnRegisteredWithStack(2942, 241);

				printf("OPC[%d] SSn[%d] is %s registered\n", 2942, 241, 
					(retVal == true)?" ":"not");

				printf("\n********isOpcSsnRegisteredWithStack-End**************\n");

				break;
			}

			case 6: {
				printf("\n********registerSASApp**************\n");
				U32 pc = 2941;
				U8 ssn = 240;
				string sasIp = "sip@19292912.rajeev.com";

				int retVal = mgr->registerSASApp(pc, ssn, sasIp);

				if (G_SUCCESS != retVal) {
					printf("Error registering SAS Application, retCode[%d]\n", retVal);
				}

				sasIp = "sip@19292912.pankaj.com";

				retVal = mgr->registerSASApp(pc, ssn, sasIp);

				if (G_SUCCESS != retVal) {
					printf("Error registering SAS Application, retCode[%d]\n", retVal);
				}

				pc = 2942;
				ssn = 241;
				sasIp = "sip@19292912.anurag.com";

				retVal = mgr->registerSASApp(pc, ssn, sasIp);

				if (G_SUCCESS != retVal) {
					printf("Error registering SAS Application, retCode[%d]\n", retVal);
				}

				printf("\n********registerSASApp-End************\n");

				break;
			}

			case 7: {
				printf("\n********de-registerSASApp**************\n");
				U32 pc = 2941;
				U8 ssn = 240;
				string sasIp = "sip@19292912.pankaj.com";

				retVal = mgr->deRegisterSASApp(pc, ssn, sasIp);

				if (G_SUCCESS != retVal) {
					printf("Error registering SAS Application, retCode[%d]\n", retVal);
				}
				printf("\n********de-registerSASApp-End************\n");

				break;
			}

			case 8: {
				printf("\n********getDestSasInfo**************\n");
					U32 pc =  2941;
					U8 ssn = 240; 

				string retVal;
				retVal = mgr->getDestSasInfo(pc, ssn);

				printf("SAS Callid [%s] \n", (true== retVal.empty())?"NULL":
				retVal.c_str());

				printf("\n********getDestSasInfo**************\n");
				break;
			}

			case 9: {
				printf("\n********debugLoadDistInfo**************\n");
				string toLog = mgr->debugLoadDistInfo();
				printf("%s\n", toLog.c_str());
				printf("\n********debugLoadDistInfo-Ends**************\n");
				break;
			}

			case 10: {
				printf("\n********removeSasApp**************\n");
				printf("Enter SAS Id to be removed: ");
				char buf[30];
				scanf("%s", buf);
				U32 pc;
				U8 ssn;

				string sasIp = buf;

				int retVal = mgr->removeSasApp(pc, ssn, sasIp);

				if (G_SUCCESS == retVal) {
					printf("PC [%d] SSN [%d] fetched for sas IP [%s]\n", 
					pc, ssn, buf);
				}
				else {
					printf("Returned error no entry exisits for sas ip[%s]\n",
					sasIp.c_str());
				}
				printf("\n********removeSasApp-Ends**************\n");

				break;
			}

			case 11: {
				printf("\n********getAllRegSasApp**************\n");
				U32 pc = 2941;
				U8 ssn = 240;

				vector<string> &retVal = mgr->getAllRegSasApp(pc, ssn);

				if (true == retVal.empty()) {
					printf("No SAS registered for pc[%d] ssn[%d]\n",
					pc, ssn);
				}
				else {
					for (int i=0; i < retVal.size(); ++i) {
						printf("The SAS registered are [%s]\n", retVal[i].c_str());
					}
				}
				printf("\n********removeSasApp-Ends**************\n");

				break;
			}

			default: {
				printf("\nInvalid entry\n");
			}
		}

	}
}
