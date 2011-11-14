package ru.mentorbank.backoffice.services.moneytransfer;

import static org.mockito.Mockito.*;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import ru.mentorbank.backoffice.dao.OperationDao;
import ru.mentorbank.backoffice.dao.exception.OperationDaoException;
import ru.mentorbank.backoffice.model.Operation;
import ru.mentorbank.backoffice.model.stoplist.JuridicalStopListRequest;
import ru.mentorbank.backoffice.model.stoplist.PhysicalStopListRequest;
import ru.mentorbank.backoffice.model.stoplist.StopListStatus;
import ru.mentorbank.backoffice.model.transfer.JuridicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.PhysicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.TransferRequest;
import ru.mentorbank.backoffice.services.accounts.AccountService;
import ru.mentorbank.backoffice.services.accounts.AccountServiceBean;
import ru.mentorbank.backoffice.services.moneytransfer.exceptions.TransferException;
import ru.mentorbank.backoffice.services.stoplist.StopListService;
import ru.mentorbank.backoffice.services.stoplist.StopListServiceStub;
import ru.mentorbank.backoffice.test.AbstractSpringTest;


public class MoneyTransferServiceTest extends AbstractSpringTest {

	@Autowired
	private MoneyTransferService moneyTransferService;
	private String SRC_AC_NUM;
	private String DST_AC_NUM;

	@Before
	public void setUp() {
		SRC_AC_NUM = "111111111111111";
		DST_AC_NUM = "222222222222222";
	}

	@Test
	public void transfer() throws TransferException, OperationDaoException {
		//fail("not implemented yet");
		// TODO (complete): Необходимо протестировать, что для хорошего перевода всё
		// работает и вызываются все необходимые методы сервисов
		// Далее следует закоментированная закотовка
		 StopListService mockedStopListService = spy(new StopListServiceStub());
		 AccountService mockedAccountService = mock(AccountServiceBean.class);
		 OperationDao mockedOperationDao = mock(OperationDao.class);
		 
		 // Создание "хорошего" перевода
		 PhysicalAccountInfo srcAccountInfo = new PhysicalAccountInfo();
		 srcAccountInfo.setAccountNumber(SRC_AC_NUM);
		 srcAccountInfo.setDocumentNumber(StopListServiceStub.DOCUMENT_NUMBER_FOR_OK_STATUS);
		 srcAccountInfo.setDocumentSeries(StopListServiceStub.DOCUMENT_SERIES_FOR_OK_STATUS);
	 
		 JuridicalAccountInfo dstAccountInfo = new JuridicalAccountInfo();
		 dstAccountInfo.setAccountNumber(DST_AC_NUM);
		 dstAccountInfo.setInn(StopListServiceStub.INN_FOR_OK_STATUS);
		 
		 TransferRequest request = new TransferRequest();
		 request.setSrcAccount(srcAccountInfo);
		 request.setDstAccount(dstAccountInfo);
		 
		 // Dynamic Stubs
		 when(mockedAccountService.verifyBalance(srcAccountInfo)).thenReturn(true);
		 
		 ((MoneyTransferServiceBean) moneyTransferService).setAccountService(mockedAccountService);
		 ((MoneyTransferServiceBean) moneyTransferService).setStopListService(mockedStopListService);
		 ((MoneyTransferServiceBean) moneyTransferService).setOperationDao(mockedOperationDao);
		 
		 moneyTransferService.transfer(request);
		 
		 // Проверка вызова методов сервиса StopListService
		 verify(mockedStopListService).getJuridicalStopListInfo(argThat(new ArgumentMatcher<JuridicalStopListRequest>() {
			 public boolean matches(Object o) {
				if (o instanceof JuridicalStopListRequest) {
					 JuridicalStopListRequest request = (JuridicalStopListRequest)o;
					 if (request.getInn() == StopListServiceStub.INN_FOR_OK_STATUS)
						 return true;
				}
				return false;
			 }
		 }));
		 
		 verify(mockedStopListService).getPhysicalStopListInfo(argThat(new ArgumentMatcher<PhysicalStopListRequest>() {
			 @Override
			 public boolean matches(Object argument) {
				if (argument instanceof PhysicalStopListRequest) {
					PhysicalStopListRequest request = (PhysicalStopListRequest)argument;
					 if ((request.getDocumentNumber() == StopListServiceStub.DOCUMENT_NUMBER_FOR_OK_STATUS) &&
							 (request.getDocumentSeries() == StopListServiceStub.DOCUMENT_SERIES_FOR_OK_STATUS))
						 return true;
				}
				return false;
			 }
		 }));
		 
		 // Проверка вызова методов сервиса AccountService
		 verify(mockedAccountService).verifyBalance(srcAccountInfo);
		 
		 // Проверка вызова OperationDao.saveOperation() для сохранения операции в таблице операций
		 verify(mockedOperationDao).saveOperation(argThat(new ArgumentMatcher<Operation>(){
			@Override
			public boolean matches(Object argument) {
				if (argument instanceof Operation) {
					Operation operation = (Operation)argument;
					if ((operation.getSrcAccount().getAccountNumber() == SRC_AC_NUM) &&
							(operation.getDstAccount().getAccountNumber() == DST_AC_NUM) &&
							(operation.getSrcStoplistInfo().getStatus() == StopListStatus.OK) &&
							(operation.getDstStoplistInfo().getStatus() == StopListStatus.OK))
						return true;
				}
				return false;
			}}));
	}
}
