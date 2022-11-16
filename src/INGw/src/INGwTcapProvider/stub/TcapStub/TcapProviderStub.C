#include <iostream.h>

#include <Util/imOid.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwLoadDistributor/INGwLdDstMgr.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwIwf/INGwIwfIface.h>
#include <INGwIwf/INGwIwfProvider.h>

#include <unistd.h>
#include <string>

using namespace std;

//string ss7AppInfo = "1-111-5,240,6,ITU92,60000,150001,15000|1-111-6,241,7,ITU92,60000,150001,15000";
string ss7AppInfo = "1-111-5,240,6,ITU92,60000,150001,15000";
string retry="10";
string retryTimer="10";
string stackIpList="192.168.13.41|12050,192.168.13.53|12050";
string selfIp="192.168.12.36";
string selfPort="12082";
string iAmPri="1";
string qSize="4";

main(int argc, const char** argv )
{
	INGwIfrPrParamRepository::getInstance().initialize(argc, argv);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwSS7_APP_INFO, ss7AppInfo);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwNUM_REGISTRATION_RETRY, retry);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwREGISTRATION_RETRY_TIMER, retryTimer);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwSTACK_IP_PORT_LIST, stackIpList);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwSTACK_IP_PORT_LIST, stackIpList);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwSELF_TCAP_IP, selfIp);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwSELF_TCAP_PORT, selfPort);
	INGwIfrPrParamRepository::getInstance().setValue
										(ingwIS_PRIMARY, iAmPri);
	INGwIfrPrParamRepository::getInstance().setValue(ingwLOAD_DIST_PATTERN, 
										 "ROUND_ROBIN");
	INGwIfrPrParamRepository::getInstance().setValue(ccmWORKER_Q_SIZE_LIMIT, 
										 qSize);
	INGwIfrPrParamRepository::getInstance().setValue(ccmWORKER_THREAD_COUNT, 
										 qSize);

	// Start Up Interface
	INGwIwfProvider::getInstance().startUp();
	INGwTcapProvider::getInstance().startUp();

	// Change State to Running
	int retVal = INGwIwfProvider::getInstance().changeState
										(INGwIwfBaseProvider::PROVIDER_STATE_RUNNING);

	if (G_SUCCESS != retVal) {
		printf("Quitting .. chnage state failed for INGwIwfProvider\n");
		fflush(stdout);
		exit (0);
	}

	retVal = INGwTcapProvider::getInstance().changeState
										(INGwIwfBaseProvider::PROVIDER_STATE_RUNNING);

	if (G_SUCCESS != retVal) {
		printf("Quitting .. chnage state failed for INGwTcapProvider\n");
		fflush(stdout);
		exit (0);
	}

	INGwIwfBaseIface *m_baseIface = INGwIwfProvider::getInstance().getInterface();
	INGwIwfIface* m_iwfIface = static_cast<INGwIwfIface*>(m_baseIface);

	sleep (15);

	while (1) {
		int choice =0;

		printf("\n\nEnter Choice..\n");
		printf("processSasInfo().....1\n");
		printf("processOutboundMsg().2\n");
		printf("getOpcSsnList()......3\n");
		printf("deregisterSas()......4\n");
		printf("processInboundMsg()..5\n");
		printf("sendSasAppResp().....6\n");
		printf("exit()...............7\n");
		scanf("%d", &choice);

		printf("*******You entered choice %d\n", choice);
		fflush(stdout);


		switch(choice) {
			case 1: {
				g_TransitObj obj;
				obj.m_sasIp = "192.168.1.111";

 				std::string inputFromSasUser;
      	inputFromSasUser += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
      	inputFromSasUser += "<tcap>\n";
      	inputFromSasUser += "    <state-req-event>\n";
      	inputFromSasUser += "        <nstate-req-event user-status=\"USER_IN_SERVICE\">\n";
      	inputFromSasUser += "            <affected-user routing-indicator=\"ROUTING_SUBSYSTEM\">\n";
      	inputFromSasUser += "                <sub-system-address sub-system-number=\"240\">\n";
      	inputFromSasUser += "                    <signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n";
      	inputFromSasUser += "                </sub-system-address>\n";
      	inputFromSasUser += "            </affected-user>\n";
      	inputFromSasUser += "        </nstate-req-event>\n";
      	inputFromSasUser += "    </state-req-event>\n";
      	inputFromSasUser += "</tcap>\n";

				m_iwfIface->processSasInfo(obj);

				if (G_INVALID_OPC_SSN == obj.m_causeCode) {
					printf("SIP SENDS .. 4XX ...\n");
					fflush(stdout);
				}
				else if (G_REG_INPROGRESS == obj.m_causeCode) {
					printf("SIP SENDS .. 200 OK ...\n");
					fflush(stdout);
				}
				else if (G_ALREADY_REG == obj.m_causeCode || G_ALREADY_DEREG == obj.m_causeCode) {
					printf("SIP SENDS .. 200 OK ...\n");
					printf("SIP GENERATES INFO 200 OK WITH FOLLOWING SDP Len[%d] XML[%s]...\n",
					obj.m_bufLen, obj.m_buf);

					fflush(stdout);
				}

				break;
			}

			case 2: {
				g_TransitObj obj;
				obj.m_sasIp = "192.168.1.111";

				obj.m_buf = new char[1099];
				obj.m_bufLen = 1099;

				strcpy(obj.m_buf, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<tcap>\n<dialogue-req-event>\n<begin-req-event dialogue-id=\"90000\" quality-of-service=\"0\">\n<component-req-event>\n<invoke-req-event class-type=\"CLASS_1\" dialogue-id=\"90000\" invoke-id=\"1\" last-component=\"true\" last-invoke-event=\"true\">\n<operation operation-type=\"OPERATIONTYPE_LOCAL\">\n<operation-code>00</operation-code>\n</operation>\n<parameters parameter-identifier=\"PARAMETERTYPE_SET\">\n<parameter>3015800104820703100810325486830703102143658719</parameter>\n</parameters>\n</invoke-req-event>\n</component-req-event>\n<destination-address national-use=\"true\" routing-indicator=\"ROUTING_SUBSYSTEM\">\n<sub-system-address sub-system-number=\"230\">\n<signaling-point-code cluster=\"111\" member=\"4\" zone=\"1\"/>\n</sub-system-address>\n</destination-address>\n<originating-address national-use=\"true\" routing-indicator=\"ROUTING_SUBSYSTEM\">\n<sub-system-address sub-system-number=\"240\">\n<signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n</sub-system-address>\n</originating-address>\n</begin-req-event>\n</dialogue-req-event>\n</tcap>");

				printf("sending %s\n", obj.m_buf);
				
				m_iwfIface->processOutboundMsg(obj);
				printf("\n*********getOpcSsnList**********\n");
				if (G_SUCCESS == obj.m_causeCode) {
					printf("SENDING MSG TO INET SUCCESSFUL..send 200OK\n");
				}
				else {
					printf("SENDING MSG TO INET FAILED..send 400 Bad Request\n");
				}
				printf("\n*********getOpcSsnList**********\n");
				fflush(stdout);

				break;
			}

			case 3: {
				g_TransitObj obj;
				obj.m_sasIp = "192.168.1.111";

				m_iwfIface->getOpcSsnList(obj);

				printf("\n*********processOutboundMsg**********\n");
				printf("[getOpcSsnList] XML Length[%d] XML[%s]\n",
				obj.m_bufLen, obj.m_buf);
				printf("\n*********processOutboundMsg**********\n");

				break;
			}
			case 7: {
				exit(0);
				break;
			}

			default: {
			}
		}
	}
}
