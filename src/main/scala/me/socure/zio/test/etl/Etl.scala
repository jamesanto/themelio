package me.socure.zio.test.etl

import monix.eval.Task

class Etl[Source, Transformed](
                                extractor: Extractor[Source],
                                transformer: Transformer[Source, Transformed],
                                loader: Loader[Transformed]
                              ) {
  def run(): Task[Unit] = {
    ???
  }
}
