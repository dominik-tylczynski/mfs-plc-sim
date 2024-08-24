package pl.sapusers.mfsplc.sim;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;

import pl.sapusers.mfsplc.Configurator;

public class test {

	public static void main(String[] args) {
		Configurator configurator = new Configurator("MFS.properties", null, null);
		JCoRecordMetaData field;

		try {
			JCoRecordMetaData handshakeMetaData = JCo.createRecordMetaData("handshake");

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSSENDER");
			handshakeMetaData.add("SENDER", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSRECEIVER");
			handshakeMetaData.add("RECEIVER", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSTELETYPE");
			handshakeMetaData.add("TELETYPE", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			field = JCoDestinationManager.getDestination(configurator.getJCoDestination()).getRepository()
					.getRecordMetaData("/SCWM/DE_MFSSN");
			handshakeMetaData.add("SEQU_NO", field.getType(0), field.getByteOffset(0), field.getUnicodeByteOffset(0),
					field);

			handshakeMetaData.lock();
			
			JCoStructure handshake = JCo.createStructure(handshakeMetaData);
			handshake.setString("EWM1    CONSYS1 LIFE00000000000000000069");

			System.out.println(handshakeMetaData);
			System.out.println(handshake);
		} catch (IllegalArgumentException | JCoException e) {
			e.printStackTrace();
		}

	}

}
