(ns pi.core)

(defn tag [tag-id seq-vecs]
  """Tag tuples with string id e.g.: [\b 5] to [1 \b 5], for id, character and count"""
  (map #(cons tag-id %) seq-vecs))

(defn str-to-char-count-and-tag [tag-id s]
  """Convert a string to a sequence of triplets tagged by the string's id
     e.g.: id=1 string='bbbbb' to [1 \b 5]"""
  (->> s
       frequencies
       (remove #(<= (second %) 1))
       (tag tag-id)))

(defn remove-non-lowercase [all-args]
  (map #(clojure.string/replace % #"[^a-z]" "") all-args))

(defn string-collection-to-id-char-freq-triplet-collection [all-args]
  (let [all-args-count (count all-args)]
    (->> all-args
         (map str-to-char-count-and-tag (range 1 (inc all-args-count)))
         (apply concat)
         (group-by second)
         (map second)
         )))

(defn sort-most-frequent-first [id-char-freq-triplet-collection]
  (let [sort-within-groups-fn #(sort (fn [[_ _ cnt1] [_ _ cnt2]] (> cnt1 cnt2)) %)]
    (map sort-within-groups-fn id-char-freq-triplet-collection)))

(defn transform-equal-frequency-ids [equal-count-resolution-fn id-char-freq-triplet-collection]
  (map equal-count-resolution-fn id-char-freq-triplet-collection))

(defn transform-to-id-char-string-tuple [id-chr-cnt-triplet-collection]
  (let [to-id-and-str-fn (fn [[id chr cnt]]
                           [id (clojure.string/join (repeat cnt chr))])
        ]
    (map to-id-and-str-fn id-chr-cnt-triplet-collection)))

(defn comparator-fn [[id1 txt1] [id2 txt2]]
  (if (= (count txt1) (count txt2))
    (compare (str id1 txt1) (str id2 txt2))
    (> (count txt1) (count txt2))))

(defn id-char-string-tuple-collection-to-final-string-result [id-char-string-tuple-collection]
  (->> id-char-string-tuple-collection
       (map #(clojure.string/join ":" %))
       (clojure.string/join "/")))

(defn mix-closure [equal-count-resolution-fn]
  """This is a closure which defines how we will output ids for characters with the same
     number of occurrences"""
  (fn [s1 s2 & args]
    (let [all-args                (concat [s1 s2] args)
          transform-ids           #(transform-equal-frequency-ids equal-count-resolution-fn %)
          take-most-frequent-only #(map first %)
          sort-by-lengths-fn      #(sort comparator-fn %)
          ]
      (->> all-args
           remove-non-lowercase
           string-collection-to-id-char-freq-triplet-collection
           sort-most-frequent-first
           transform-ids
           take-most-frequent-only
           transform-to-id-char-string-tuple
           sort-by-lengths-fn
           id-char-string-tuple-collection-to-final-string-result
           ))))

(defn resolution-return-equal [sorted-seq-of-triplets]
  """Replace the string ids for '=' in the first two triplets for a given character"""
  (if (> (count sorted-seq-of-triplets) 1)
    (let [[_ chr cnt1] (first sorted-seq-of-triplets)
          [_ _   cnt2] (second sorted-seq-of-triplets)
          ]
      (if (= cnt1 cnt2)
        (repeat 2 ["=" chr cnt1])
        sorted-seq-of-triplets))
    sorted-seq-of-triplets))

(defn resolution-return-list-of-ids [sorted-seq-of-triplets]
  """Find which string ids are equal to the maximum of occurrences for a given character
     and replace each of them with the list of all of them"""
  (if (> (count sorted-seq-of-triplets) 1)
    (let [[_ _ max-count]         (first sorted-seq-of-triplets)
          triplets-with-max-freq  (filter (fn [[_ _ freq]] (= max-count freq)) sorted-seq-of-triplets)
          set-of-ids-with-max-cnt (set (map first triplets-with-max-freq))
          id-to-list-of-ids-fn    (fn [[id chr cnt]]
                                    (if (contains? set-of-ids-with-max-cnt id)
                                      [(clojure.string/join "," (sort set-of-ids-with-max-cnt)) chr cnt]
                                      [id chr cnt]))
          ]
      (map id-to-list-of-ids-fn sorted-seq-of-triplets))
    sorted-seq-of-triplets))

(defn mix
  ([s1 s2]
   """Main entry point for the two argument call"""
   (let [mix-internal (mix-closure resolution-return-equal)]
      (mix-internal s1 s2)
      ))
  ([s1 s2 s3 & others]
   """Main entry point for the multiple argument call"""
   (let [mix-internal (mix-closure resolution-return-list-of-ids)]
     (apply mix-internal (concat [s1 s2 s3] others))
     )))