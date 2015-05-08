package eu.claudiumihaila.nlp.uima;

import java.util.Iterator;

import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class Application {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// prepares a listener for when the analysis engine is complete
		UimaAsBaseCallbackListener asyncListener = new UimaAsBaseCallbackListener() {
			/**
			 * This will be called once the text is processed.
			 */
			@Override
			public void entityProcessComplete(CAS output,
					EntityProcessStatus aStatus) {
				try {
					final JCas jcas = output.getJCas();

					AnnotationIndex<Annotation> annIndex = jcas
							.getAnnotationIndex(Annotation.type);
					Iterator<Annotation> annIt = annIndex.iterator();
					Annotation ann = null;

					System.out.println(jcas.getDocumentText());
					while (annIt.hasNext()) {
						ann = (Annotation) annIt.next();
						System.out.println(ann.getType());
					}
				} catch (CASRuntimeException e) {
					e.printStackTrace();
				} catch (CASException e) {
					e.printStackTrace();
				}
			}
		};

		// constructs a class to create and run a UIMA pipeline
		Pipeline uimaPipeline = new Pipeline(asyncListener);

		for (int i = 0; i < 100; i++) {
			uimaPipeline.process("This is text number " + i + ".");
		}
	}

}
