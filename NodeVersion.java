import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NodeVersion {
	public static void main (String args[]) {
		
		String major = null, minor = null, patch = null;

		try {
			BufferedReader in = new BufferedReader(new FileReader(args[0] + "/src/node_version.h"));
			String line;

			while ((line = in.readLine()) != null) {
				if (line.indexOf("define NODE_MAJOR_VERSION ") > -1) {
					major = line.split(" NODE_MAJOR_VERSION ")[1].trim();
				} else if (line.indexOf("define NODE_MINOR_VERSION ") > -1) {
					minor = line.split(" NODE_MINOR_VERSION ")[1].trim();
				} else if (line.indexOf("define NODE_PATCH_VERSION ") > -1) {
					patch = line.split(" NODE_PATCH_VERSION ")[1].trim();
				}
			}

			in.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		if (major != null && minor != null && patch != null) {
			String nodeVersion = major + "." + minor + "." + patch;
			System.out.println("Node.js version: " + nodeVersion);
			System.out.println(nodeVersion.split("\\.")[0]);
		}
	}
}