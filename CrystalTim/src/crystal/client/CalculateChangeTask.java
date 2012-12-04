package crystal.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import crystal.util.RunIt;
import crystal.util.RunIt.Output;
import difflib.DiffRow;
import difflib.DiffRowGenerator;

public class CalculateChangeTask {

	public static class ChangeItem {
		ArrayList<String> changedFiles;
		ArrayList<String> changedFilesContents;

		ChangeItem() {
			changedFiles = new ArrayList<String>();
			changedFilesContents = new ArrayList<String>();
		}
	}

	private static List<String> fileToLines(String filename) {
		List<String> lines = new ArrayList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			// File not found
			File f = new File(filename);
			try {
				f.createNewFile();
			} catch (IOException e1) {
				return lines;
			}
		}
		return lines;

	}

	public static ChangeItem performTask(String _gitPath, String myDir,
			String yourDir) throws IOException {

		// ChangedFilesSet is a union set for storing all changed files
		// Files are uniquely existing in the set
		Output exeResult;
		String[] ChangedFiles;
		HashSet<Object> ChangedFilesSet = new HashSet<Object>();

		// Prepare the command
		String[] gitFilesArgs = { "log", "-1", "--pretty=format:\"\"",
				"--name-only" };

		// Execute command in myDir
		exeResult = RunIt.execute(_gitPath, gitFilesArgs, myDir, false);
		ChangedFiles = exeResult.getOutput().split("\n");
		for (String s : ChangedFiles)
			if (!s.equals(""))
				ChangedFilesSet.add(s);

		// Execute command in yourDir
		exeResult = RunIt.execute(_gitPath, gitFilesArgs, yourDir, false);
		ChangedFiles = exeResult.getOutput().split("\n");
		for (String s : ChangedFiles)
			if (!s.equals(""))
				ChangedFilesSet.add(s);

		ChangeItem files = new ChangeItem();

		if (!myDir.endsWith(File.separator))
			myDir = myDir + File.separator;

		if (!yourDir.endsWith(File.separator))
			yourDir = yourDir + File.separator;

		int i = 0;
		for (Object s : ChangedFilesSet) {

			List<String> myFileLines = fileToLines(myDir + s);
			List<String> urFileLines = fileToLines(yourDir + s);

			DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
			builder.showInlineDiffs(false);
			DiffRowGenerator dfg = builder.build();
			List<DiffRow> rows = dfg.generateDiffRows(myFileLines, urFileLines);

			String contentLines = "";
			for (DiffRow dr : rows) {
				if (dr.getTag() == DiffRow.Tag.DELETE)
					// System.out.println("-\t" + dr.getOldLine());
					contentLines = contentLines + "-\t" + dr.getOldLine()
							+ "\n";
				else if (dr.getTag() == DiffRow.Tag.INSERT)
					// System.out.println("+\t" + dr.getNewLine());
					contentLines = contentLines + "+\t" + dr.getNewLine()
							+ "\n";
				else if (dr.getTag() == DiffRow.Tag.CHANGE) {
					// System.out.println("+-\t" + dr.getNewLine());
					contentLines = contentLines + "+\t" + dr.getNewLine()
							+ "\n";
					contentLines = contentLines + "-\t" + dr.getOldLine()
							+ "\n";
				}
			}
			files.changedFiles.add(i, (String) s);
			files.changedFilesContents.add(i, contentLines);
			i++;
		}
		return files;
	}

}