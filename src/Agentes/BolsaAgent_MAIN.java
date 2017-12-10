package Agentes;

import Auxiliar.Setup;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import javax.swing.*;

public class BolsaAgent_MAIN {

	public static void main(String[] args) {
		String[] argsStarter = new String[]
				{
						"-gui", "false",
						"-welcome", "false",
						"-cli", "false",
						"-printpass", "false"
				};

		final ThreadSuspendable sus = new ThreadSuspendable();
		final IExternalAccess platform = Starter.createPlatform(argsStarter).get(sus);
		IComponentManagementService cms = SServiceProvider.getService(platform.getServiceProvider(),
				IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		final Setup setup = new Setup(cms, sus, platform);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setup.setTitle("Setup");
				setup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				setup.setSize(800, 800);
				setup.pack();
				setup.setVisible(true);
			}
		});


	}

}
