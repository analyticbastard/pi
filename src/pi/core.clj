(ns pi.core)

(defn str-to-char-count [s]
  (->> s
       frequencies
       (remove #(<= (second %) 1))
       ))

(defn tag [tag-id seq-vecs]
  (map #(cons tag-id %) seq-vecs))

(defn str-to-char-count-and-tag [tag-id s]
  (->> s
       str-to-char-count
       (tag tag-id)))

(defn mix-closure [equal-count-resolution-fn]
  (fn [s1 s2 & args]
    (let [sort-within-groups-fn #(sort (fn [[_ _ cnt1] [_ _ cnt2]] (> cnt1 cnt2)) %)
          to-id-and-str-fn      (fn [[id chr cnt]]
                                  [id (clojure.string/join (repeat cnt chr))])
          sort-by-lengths-fn    #(sort (fn [[id1 txt1] [id2 txt2]]
                                         (if (= (count txt1) (count txt2))
                                           (compare (str id1) (str id2)) ;(compare txt1 txt2)
                                           (> (count txt1) (count txt2)))) %)
          all-args              (concat [s1 s2] args)
          all-args              (map #(clojure.string/replace % #"[^a-z]" "") all-args)
          all-args-count        (count all-args)
          ]
      (->> all-args
           (map str-to-char-count-and-tag (range 1 (inc all-args-count)))
           (apply concat)
           (group-by second)
           (map second)
           (map sort-within-groups-fn)
           (map equal-count-resolution-fn)
           (map first)
           (map to-id-and-str-fn)
           sort-by-lengths-fn
           (map #(clojure.string/join ":" %))
           (clojure.string/join "/")
           ))))

(defn resolution-return-equal [sorted-seq-of-triplets]
  (if (> (count sorted-seq-of-triplets) 1)
    (let [[_ chr cnt1] (first sorted-seq-of-triplets)
          [_ _   cnt2] (second sorted-seq-of-triplets)
          ]
      (if (= cnt1 cnt2)
        (repeat 2 ["=" chr cnt1])
        sorted-seq-of-triplets))
    sorted-seq-of-triplets))

(defn resolution-return-list-of-ids [sorted-seq-of-triplets]
  (if (> (count sorted-seq-of-triplets) 1)
    (let [[_ _ max-count]         (first sorted-seq-of-triplets)
          set-of-ids-with-max-cnt (atom #{})
          _                       (doseq [[id _ cnt] sorted-seq-of-triplets]
                                    (when (= cnt max-count)
                                      (swap! set-of-ids-with-max-cnt conj id)))
          id-to-list-of-ids-fn    (fn [[id chr cnt]]
                                    (if (contains? @set-of-ids-with-max-cnt id)
                                      [(clojure.string/join "," (sort @set-of-ids-with-max-cnt)) chr cnt]
                                      [id chr cnt]))
          ]
      (map id-to-list-of-ids-fn sorted-seq-of-triplets))
    sorted-seq-of-triplets))

(defn mix
  ([s1 s2]
   (let [mix-internal (mix-closure resolution-return-equal)]
      (mix-internal s1 s2)
      ))
  ([s1 s2 s3 & others]
   (let [mix-internal (mix-closure resolution-return-list-of-ids)]
     (apply mix-internal (concat [s1 s2 s3] others))
     )))