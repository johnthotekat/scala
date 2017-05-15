package strawman
package collection

import strawman.collection.immutable.TreeMap

import scala.Ordering

/** Base type of sorted sets */
trait SortedMap[K, +V]
  extends Map[K, V]
    with SortedMapOps[K, V, SortedMap, SortedMap[K, V]]

trait SortedMapOps[K, +V, +CC[X, Y] <: SortedMap[X, Y] with SortedMapOps[X, Y, CC, _], +C <: SortedMap[K, V]]
  extends MapOps[K, V, Map, C]
     with SortedOps[K, C] {

  protected[this] def orderedMapFromIterable[K2, V2](it: collection.Iterable[(K2, V2)])(implicit ordering: Ordering[K2]): CC[K2, V2]

  def firstKey: K = head._1
  def lastKey: K = last._1

  // And finally, we add new overloads taking an ordering
  def map[K2, V2](f: ((K, V)) => (K2, V2))(implicit ordering: Ordering[K2]): CC[K2, V2] =
    orderedMapFromIterable(View.Map[(K, V), (K2, V2)](coll, f))

  def flatMap[K2, V2](f: ((K, V)) => IterableOnce[(K2, V2)])(implicit ordering: Ordering[K2]): CC[K2, V2] =
    orderedMapFromIterable(View.FlatMap(coll, f))

  def ++[K2 >: K, V2 >: V](xs: IterableOnce[(K2, V2)])(implicit ordering: Ordering[K2]): CC[K2, V2] =
    orderedMapFromIterable(View.Concat(coll, xs))
}

object SortedMap extends OrderedMapFactory.Delegate[SortedMap](TreeMap)
