package gehos.configuracion.management.utilidades;

import org.jboss.seam.annotations.Name;

@Name("parameters")
public class Parameters {
	/* á é í ó ú */
	private String[] vocalesAcentuadasMin = { "á", "é", "í", "ó", "ú", "ñ" };
	private String[] vocalesAcentuadasMinCodec = { "&#225;", "&#233;",
			"&#237;", "&#243; ", "&#250;", "&#241;" };

	private String[][] caracteresExt;

	// METODOS-------------------------------------------------
	public void inicializarCaracteresExtranos() {
		caracteresExt = new String[13][2];

		caracteresExt[0][0] = "ÃÂ";
		caracteresExt[0][1] = "Á";
		caracteresExt[1][0] = "ÃÂ";
		caracteresExt[1][1] = "É";
		caracteresExt[2][0] = "ÃÂ";
		caracteresExt[2][1] = "Í";
		caracteresExt[3][0] = "ÃÂ";
		caracteresExt[3][1] = "Ó";
		caracteresExt[4][0] = "ÃÂ";
		caracteresExt[4][1] = "Ú";
		caracteresExt[5][0] = "ÃÂ";
		caracteresExt[5][1] = "Ñ";
		caracteresExt[6][0] = "ÃÂ±";
		caracteresExt[6][1] = "ñ";
		caracteresExt[7][0] = "ÃÂ¡";
		caracteresExt[7][1] = "á";
		caracteresExt[8][0] = "ÃÂ©";
		caracteresExt[8][1] = "é";
		caracteresExt[9][0] = "ÃÂ³";
		caracteresExt[9][1] = "ó";
		caracteresExt[10][0] = "ÃÂº";
		caracteresExt[10][1] = "ú";
		caracteresExt[11][0] = "ÃÂ";
		caracteresExt[11][1] = "í";
		caracteresExt[12][0] = "Ã³";
		caracteresExt[12][1] = "ó";

	}

	public String parametroURL(String param) {
		if (!param.equals("")) {
			inicializarCaracteresExtranos();
			int uno = caracteresExt.length;			
			for (int i = 0; i < uno; i++) {
				if (param.contains(caracteresExt[i][0]))
					param = param.replaceAll(caracteresExt[i][0],
							caracteresExt[i][1]);
			}
			return param;
		}
		return "";
	}

	public String encodec(String param) {
		if (!param.equals("")) {
			for (int i = 0; i < vocalesAcentuadasMin.length; i++) {
				if (param.contains(vocalesAcentuadasMin[i])) {
					param = param.replaceAll(vocalesAcentuadasMin[i],
							vocalesAcentuadasMinCodec[i]);
				}

			}
			return param;
		}
		return "";
	}
	
	public String decodec(String param) {
		if (!param.equals("")) {
			for (int i = 0; i < vocalesAcentuadasMinCodec.length; i++) {
				if (param.contains(vocalesAcentuadasMinCodec[i])) {
					param = param.replaceAll(vocalesAcentuadasMinCodec[i],
							vocalesAcentuadasMin[i]);
				}

			}
			return param;
		}
		return "";
	}

}
