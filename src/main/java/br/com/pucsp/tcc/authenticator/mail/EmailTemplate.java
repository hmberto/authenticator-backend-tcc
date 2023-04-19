package br.com.pucsp.tcc.authenticator.mail;

public class EmailTemplate {
	
	private EmailTemplate () {}
	
	public static String template(String messageSubject, String info, String shortText, String btnText, String btnLink) {
		return "" 
				+ "<!DOCTYPE html>\n"
				+ "<html lang=\"pt-br\">\n"
				+ "<head>\n"
				+ "  <meta charset=\"UTF-8\">\n"
				+ "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
				+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
				+ "  <title>" + messageSubject + "</title>\n"
				+ "</head>\n"
				+ "<body>\n"
				+ "  <div role=\"banner\">\n"
				+ "    <div class=\"header\" style=\"Margin: 0 auto;max-width: 600px;min-width: 320px; width: 320px;width: calc(28000% - 167400px);\" id=\"emb-email-header-container\">\n"
				+ "      <div class=\"logo emb-logo-margin-box\" style=\"font-size: 26px;line-height: 32px;Margin-top: 16px;Margin-bottom: 32px;color: #41637e;font-family: sans-serif;Margin-left: 20px;Margin-right: 20px;\" align=\"center\">\n"
				+ "        <div class=\"logo-center\" align=\"center\" id=\"emb-email-header\"><img style=\"display: block;height: auto;width: 100%;border: 0;max-width: 141px;\" src=\"https://uploaddeimagens.com.br/images/004/431/621/full/security.png?1681512501\" alt width=\"141\"></div>\n"
				+ "      </div>\n"
				+ "    </div>\n"
				+ "  </div>\n"
				+ "  <div>\n"
				+ "    <div class=\"layout one-col fixed-width stack\" style=\"Margin: 0 auto;max-width: 600px;min-width: 320px; width: 320px;width: calc(28000% - 167400px);overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;\">\n"
				+ "    <div class=\"layout__inner\" style=\"border-collapse: collapse;display: table;width: 100%;background-color: #ffffff;\">\n"
				+ "    <div class=\"column\" style=\"text-align: left;color: #717a8a;font-size: 16px;line-height: 24px;font-family: sans-serif;\">\n"
				+ "    <div style=\"Margin-left: 10px;Margin-right: 10px;Margin-top: 24px;\">\n"
				+ "  </div>\n"
				+ "  <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n"
				+ "    <h1 style=\"Margin-top: 0;Margin-bottom: 20px;font-style: normal;font-weight: normal;color: #3d3b3d;font-size: 26px;line-height: 30px;text-align: center;\">\n"
				+ "      " + shortText + "\n"
				+ "    </h1>\n"
				+ "  </div>\n"
				+ "  <div style=\"Margin-left: 10px;Margin-right: 10px;\">\n"
				+ "    <h2 class=\"size-24\" style=\"Margin-top: 0;Margin-bottom: 16px;font-style: normal;font-weight: normal;color: #3d3b3d;font-size: 16px;line-height: 24px;text-align: center;\" lang=\"x-size-24\">\n"
				+ "      " + info + "<br><br>\n"
				+ "    </h2>\n"
				+ "  </div>\n"
				+ "  <div style=\"Margin-left: 10px;Margin-right: 10px;\">\n"
				+ "    <div class=\"btn btn--flat btn--large\" style=\"Margin-bottom: 20px;text-align: center;\">\n"
				+ "      <a style=\"border-radius: 4px;display: inline-block;font-size: 14px;font-weight: bold;line-height: 24px;padding: 12px 24px;text-align: center;text-decoration: none !important;transition: opacity 0.1s ease-in;color: #ffffff !important;background-color: #337ab7;font-family: sans-serif;\" href=\"" + btnLink + "\" target=\"_blank\">\n"
				+ "        " + btnText + "\n"
				+ "      </a>\n"
				+ "  </div>\n"
				+ "</body>\n"
				+ "</html>";
	}
}