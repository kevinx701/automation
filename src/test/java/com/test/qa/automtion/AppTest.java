package com.test.qa.automtion;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.qa.automtion.datafactory.DataProviderArguments;
import com.test.qa.automtion.datafactory.ExcelDataProvider;

public class AppTest {

	public AppTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass(alwaysRun = true)
	public void setup() throws Exception {

	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {

	}

	@SuppressWarnings("rawtypes")
	@Test(groups = "Functional", dataProviderClass = ExcelDataProvider.class, dataProvider = "getData")
	@DataProviderArguments({ "filePath=src/test/resources/assetlist.xls", "sheetName=negative_cases" })
	public void test02(HashMap Data) {
		System.out.println(Data.toString());
		
	}
}
