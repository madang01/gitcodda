/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.codda.main;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.EmptyResource;
import org.eclipse.jetty.util.resource.Resource;

import kr.codda.servlet.CurrentWokingPathChangerSvl;
import kr.codda.servlet.CurrentWokingPathInformationGetterSvl;
import kr.codda.servlet.ErrorMessageGetterSvl;
import kr.codda.servlet.HelloServlet;
import kr.codda.servlet.ProjectManagerSvl;

/**
 * @author Won Jonghoon
 *
 */
public class HelperServer {

	public static Server createServer(int port, Resource baseResource) throws Exception {
		Server server = new Server(port);

		ServletHandler servletHandler = new ServletHandler();

		servletHandler.addServletWithMapping(HelloServlet.class, "/servlet/Hello");

		servletHandler.addServletWithMapping(ProjectManagerSvl.class, "/servlet/ProjectManager");
		servletHandler.addServletWithMapping(ErrorMessageGetterSvl.class, "/servlet/ErrorMessageGetter");
		servletHandler.addServletWithMapping(CurrentWokingPathInformationGetterSvl.class, "/servlet/CurrentWokingPathInformationGetter");
		servletHandler.addServletWithMapping(CurrentWokingPathChangerSvl.class, "/servlet/CurrentWokingPathChanger");

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setBaseResource(Resource.newClassPathResource("webapp"));
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });
		server.setHandler(handlers);

		return server;
	}

	public static void main(String[] args) throws Exception {
		int port = 8090;

		if (0 == args.length) {

		} else if (1 == args.length) {
			String portParmeter = args[0];
			try {
				Integer.parseInt(portParmeter);
			} catch (NumberFormatException e) {
				Logger.getGlobal().severe("the parameter port[" + portParmeter + "] is not a integr");
				System.exit(1);
			}
		} else {
			Logger.getGlobal().severe("the var args.length is not a one");
			System.exit(1);
		}

		/*
		 * Path userDir = Paths.get(System.getProperty("user.dir")); PathResource
		 * pathResource = new PathResource(userDir);
		 */

		Server server = createServer(port, EmptyResource.INSTANCE);

		// Start things up! By using the server.join() the server thread will join with
		// the current thread.
		// See
		// "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()"
		// for more details.
		server.start();
		server.join();
	}
}
