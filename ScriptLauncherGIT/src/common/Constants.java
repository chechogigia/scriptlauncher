package common;

import java.util.Arrays;
import java.util.List;

public class Constants {

	public static final int MODE_SINC = 1;
	public static final int MODE_COPY = 2;
	public static final int MODE_LIST = 3;

	public static final String PrjRegex = ".*\\.prj";
	public static final String DirRegex = ".Files -.*";
	public static final String FileRegex = ".*=.*";

	public static final String PROJECT_FILE_NAME = "Proyecto.prj";
	public static final String IMPDIR_NAME = "3. Implementación/";
	public static final String DOCDIR_NAME = "5. Documentacion/";
	public static final String SELECTED_FILES_NAME = "SelectedFiles.txt";
	public static final String ULTIMA_SINC_NAME = "UltimaSincronizacion.txt";

	public static final String CheckmetaUpstream = "Checkmeta Upstream";
	public static final String CheckmetaBuyer = "Checkmeta Buyer";
	public static final String CompCoreUpstream = "Compilar Core Upstream";
	public static final String CompCoreBuyer = "Compilar Core Buyer";
	public static final String CompExtensionsUpstream = "Compilar Extensiones Upstream";
	public static final String CompExtensionsBuyer = "Compilar Extensiones Buyer";
	public static final String InitDBUpstream = "InitDB Reshape Upstream";
	public static final String InitDBBuyer = "InitDB Reshape Buyer";

	public static final List<String> availableRemoteCommands = Arrays.asList(new String[] { CheckmetaUpstream, CheckmetaBuyer, CompCoreUpstream, CompCoreBuyer, CompExtensionsUpstream, CompExtensionsBuyer, InitDBUpstream, InitDBBuyer });

	public static String getIdForCommandName(String commandName) {
		if (CheckmetaUpstream.equals(commandName))
			return "chu";
		if (CheckmetaBuyer.equals(commandName))
			return "chb";
		if (CompCoreUpstream.equals(commandName))
			return "cucore";
		if (CompCoreBuyer.equals(commandName))
			return "cbcore";
		if (CompExtensionsUpstream.equals(commandName))
			return "cucust";
		if (CompExtensionsBuyer.equals(commandName))
			return "cbcust";
		if (InitDBUpstream.equals(commandName))
			return "iu";
		if (InitDBBuyer.equals(commandName))
			return "ib";
		return null;
	}
}
