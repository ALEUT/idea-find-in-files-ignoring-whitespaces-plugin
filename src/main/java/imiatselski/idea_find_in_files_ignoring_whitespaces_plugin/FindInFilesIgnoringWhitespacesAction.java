package imiatselski.idea_find_in_files_ignoring_whitespaces_plugin;

import org.jetbrains.annotations.NotNull;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class FindInFilesIgnoringWhitespacesAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
		CaretModel caretModel = editor.getCaretModel();
		String selectedText = caretModel.getCurrentCaret().getSelectedText();

		if (selectedText != null) {
			String regexp = escapeToRegexp(selectedText);
			regexp = regexp.replaceAll("\\\\n\\s*", "\\\\n\\\\s*").replaceAll("\\s+", "\\\\s+");

			Project project = e.getRequiredData(CommonDataKeys.PROJECT);
			FindManager findManager = FindManager.getInstance(project);
			FindModel findModel = findManager.getFindInProjectModel().clone();
			findModel.setReplaceState(false);
			findModel.setStringToFind(regexp);
			findModel.setRegularExpressions(true);

			FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
			findInProjectManager.findInProject(e.getDataContext(), findModel);
		}
	}

	@Override
	public void update(AnActionEvent e) {
		Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
		CaretModel caretModel = editor.getCaretModel();
		e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
	}

	private static String escapeToRegexp(String text) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
				result.append(c);
			} else if (c == '\n') {
				result.append("\\n");
			} else if (Character.isWhitespace(c)) {
				result.append(' ');
			} else {
				result.append('\\').append(c);
			}
		}

		return result.toString();
	}

	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread() {
		return ActionUpdateThread.BGT;
	}
}
