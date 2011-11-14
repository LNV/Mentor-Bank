package ru.mentorbank.backoffice.services.moneytransfer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import ru.mentorbank.backoffice.dao.exception.OperationDaoException;
import ru.mentorbank.backoffice.model.stoplist.StopListStatus;
import ru.mentorbank.backoffice.model.transfer.JuridicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.PhysicalAccountInfo;
import ru.mentorbank.backoffice.model.transfer.TransferRequest;
import ru.mentorbank.backoffice.services.accounts.AccountService;
import ru.mentorbank.backoffice.services.accounts.AccountServiceBean;
import ru.mentorbank.backoffice.services.moneytransfer.exceptions.TransferException;
import ru.mentorbank.backoffice.services.stoplist.StopListServiceStub;
import ru.mentorbank.backoffice.test.AbstractSpringTest;

public class MoneyTransferServiceFailsWithAskSecurityStopListStatusTest extends
		AbstractSpringTest {

	@Autowired
	private MoneyTransferServiceBean moneyTransferService;
	private AccountService mockedAccountService;
	private JuridicalAccountInfo srcAccountInfo;
	private JuridicalAccountInfo dstJuridicalAccountInfo;
	private PhysicalAccountInfo dstPhysicalAccountInfo;

	@Before
	public void setUp() {

		srcAccountInfo = new JuridicalAccountInfo();
		srcAccountInfo.setAccountNumber("111111111111111");
		srcAccountInfo.setInn(StopListServiceStub.INN_FOR_OK_STATUS);

		dstJuridicalAccountInfo = new JuridicalAccountInfo();
		dstJuridicalAccountInfo.setAccountNumber("222222222222222");
		dstJuridicalAccountInfo.setInn(StopListServiceStub.INN_FOR_ASKSECURITY_STATUS);
		
		dstPhysicalAccountInfo = new PhysicalAccountInfo();
		dstPhysicalAccountInfo.setAccountNumber("333333333333333");
		dstPhysicalAccountInfo.setDocumentNumber(StopListServiceStub.DOCUMENT_NUMBER_FOR_ASKSECURITY_STATUS);
		dstPhysicalAccountInfo.setDocumentSeries(StopListServiceStub.DOCUMENT_SERIES_FOR_OK_STATUS);

		mockedAccountService = mock(AccountServiceBean.class);
		// Dynamic Stub
		when(mockedAccountService.verifyBalance(srcAccountInfo)).thenReturn(true);
		moneyTransferService.setAccountService(mockedAccountService);
	}

	@Test
	@ExpectedException(TransferException.class)
	public void transfer_JuridicalFailsWithAskSecurityStopListStatus()
			throws TransferException, OperationDaoException {
		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setSrcAccount(srcAccountInfo);
		transferRequest.setDstAccount(dstJuridicalAccountInfo);
		moneyTransferService.transfer(transferRequest);
	}
	
	@Test	
	@ExpectedException(TransferException.class)
	public void transfer_PhysicalFailsWithAskSecurityStopListStatus1()
			throws TransferException, OperationDaoException {
		// TODO (new, complete): Проверка физ.лиц по стоп-листам.
		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setSrcAccount(srcAccountInfo);
		transferRequest.setDstAccount(dstPhysicalAccountInfo);
		moneyTransferService.transfer(transferRequest);
	}
}
