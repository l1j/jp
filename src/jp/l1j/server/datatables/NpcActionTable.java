/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jp.l1j.server.datatables;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.npc.action.L1NpcAction;
import jp.l1j.server.model.npc.action.L1NpcXmlParser;
import jp.l1j.server.utils.FileUtil;
import jp.l1j.server.utils.PerformanceTimer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class NpcActionTable {
	private static Logger _log = Logger.getLogger(NpcActionTable.class.getName());
	
	private static NpcActionTable _instance;
	
	private static List<L1NpcAction> _actions = new ArrayList<L1NpcAction>();
	
	private static List<L1NpcAction> _talkActions = new ArrayList<L1NpcAction>();

	public static NpcActionTable getInstance() {
		if (_instance == null) {
			_instance = new NpcActionTable();
		}
		return _instance;
	}

	private NpcActionTable() {
		loadAllActions(_actions, _talkActions);
	}

	private List<L1NpcAction> loadActions(File file, String nodeName)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(file);
		if (!doc.getDocumentElement().getNodeName().equalsIgnoreCase(nodeName)) {
			return new ArrayList<L1NpcAction>();
		}
		return L1NpcXmlParser.listActions(doc.getDocumentElement());
	}

	private void loadDirectoryActions(List<L1NpcAction> actions,
			List<L1NpcAction> talkActions, File dir) throws Exception {
		for (String file : dir.list()) {
			File f = new File(dir, file);
			if (FileUtil.getExtension(f).equalsIgnoreCase("xml")) {
				actions.addAll(loadActions(f, "NpcActionList"));
				talkActions.addAll(loadActions(f, "NpcTalkActionList"));
			}
		}
	}

	public void loadAllActions(List<L1NpcAction> actions,
			List<L1NpcAction> talkActions) {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			File usersDir = new File("./data/xml/NpcActions/users/");
			if (usersDir.exists()) {
				loadDirectoryActions(actions, talkActions, usersDir);
			}
			loadDirectoryActions(actions, talkActions, new File("./data/xml/NpcActions/"));
			System.out.println("loading npc actions...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (Exception e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_FAILED, "NpcAction"), e);
			System.exit(0);
		}
	}

	public void reload() {
		List<L1NpcAction> actions = new ArrayList<L1NpcAction>();
		List<L1NpcAction> talkActions = new ArrayList<L1NpcAction>();
		loadAllActions(actions, talkActions);
		_actions = actions;
		_talkActions = talkActions;
	}
	
	public L1NpcAction get(String actionName, L1PcInstance pc, L1Object obj) {
		for (L1NpcAction action : _actions) {
			if (action.acceptsRequest(actionName, pc, obj)) {
				return action;
			}
		}
		return null;
	}

	public L1NpcAction get(L1PcInstance pc, L1Object obj) {
		for (L1NpcAction action : _talkActions) {
			if (action.acceptsRequest("", pc, obj)) {
				return action;
			}
		}
		return null;
	}
}
